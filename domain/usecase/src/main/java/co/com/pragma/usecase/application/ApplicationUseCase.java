package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.*;
import co.com.pragma.model.application.dto.PageResponse;
import co.com.pragma.usecase.application.exception.ApplicationNotFoundException;
import co.com.pragma.usecase.application.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@RequiredArgsConstructor
public class ApplicationUseCase {



    private static final String STATUS_PENDIENTE = "Pendiente";
    private static final String STATUS_APROBADA = "Aprobado";
    private static final String STATUS_RECHAZADA = "Rechazada";
    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final ApplicationValidator validator;
    private final CatalogCachePort catalogCachePort;
    private final QueueSender queueSender;
    private final LogPort log;

    public Mono<Application> saveApplication(Application application, String userIdentification) {
        return Mono.just(application)
                .doOnNext(app -> app.setStatus(catalogCachePort.getStatusIdByName("PENDIENTE")))
                .doOnNext(validator::validateFields)
                .flatMap(appVerified ->
                        catalogCachePort.getLoanTypeById(appVerified.getType())
                                .switchIfEmpty(Mono.error(new ValidationException(List.of("Loan Type not found"))))
                                .map(type -> {
                                    validator.validateAmount(appVerified, type);
                                    return appVerified;
                                })
                )
                .flatMap(appVerified ->
                    userService.getUserByIdentification(userIdentification)
                            .switchIfEmpty(Mono.error(new ValidationException(List.of("User not found"))))
                            .map(user -> {
                                appVerified.setUserId(user.getId());
                                return appVerified;
                            })
                )
                .flatMap(applicationRepository::saveApplication)
                .flatMap(savedApp ->
                    catalogCachePort.getLoanTypeById(application.getType())
                        .flatMap(loanType -> {
                            if (Boolean.TRUE.equals(loanType.getAutoValidation())) {
                                try {
                                    List<Application> approvedApps = (List<Application>) applicationRepository.findAllByUserIdAndStatusId(application.getUserId(), catalogCachePort.getStatusIdByName(STATUS_APROBADA));
                                    return queueSender.sendApplicationForAutomaticValidation(savedApp, approvedApps)
                                            .thenReturn(savedApp);
                                } catch (Exception e) {
                                    log.error("Error fetching approved applications for userId: " + application.getUserId() + e);
                                    return Mono.just(savedApp);
                                }

                            } else {
                                return Mono.just(savedApp);
                            }
                        })
                );
    }

    public Mono<Application> getApplication(BigInteger id) {
        if (id == null) throw new ValidationException(List.of("Id is required"));
        return applicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationNotFoundException("ERROR_FETCHING_USER_BY_ID")));
    }

    public Mono<PageResponse<Application>> findApplications(Integer page, Integer size, String status) {
        if (page == null || page < 0) throw new ValidationException(List.of("Page must be greater than or equal to 0"));
        if (size == null || size <= 0) throw new ValidationException(List.of("Size must be greater than 0"));
        Long statusId = (status != null) ? catalogCachePort.getStatusIdByName(status) : null;
        if (status != null && statusId == null) {
            throw new ValidationException(List.of("Status not found"));
        }
        return applicationRepository.findAll(page, size, statusId);
    }

    public Mono<Void> actionApplication(Long id, String action) {
        log.info("actionApplication called with id: " + id + " and action: " + action);
        if ("APPROVE".equals(action) || "REJECT".equals(action)) {
            return getApplication(BigInteger.valueOf(id))
                    .doOnSubscribe(sub -> log.info("Fetching application with id: " + id))
                    .flatMap(application -> {
                        log.info("Application fetched: " + application);
                        Long pendienteStatus = catalogCachePort.getStatusIdByName(STATUS_PENDIENTE);
                        log.info("Status PENDIENTE id: " + pendienteStatus);
                        if (!application.getStatus().equals(pendienteStatus)) {
                            log.warn("Application status is not PENDIENTE. Current status: " + application.getStatus());
                            return Mono.error(new ValidationException(List.of("Only applications with PENDIENTE status can be processed")));
                        }
                        Long newStatusId = "APPROVE".equals(action) ?
                                catalogCachePort.getStatusIdByName(STATUS_APROBADA) :
                                catalogCachePort.getStatusIdByName(STATUS_RECHAZADA);
                        log.info("Setting new status id: " + newStatusId + " for action: " + action);
                        application.setStatus(newStatusId);
                        return applicationRepository.updateApplication(application)
                                .doOnSuccess(updatedApp -> log.info("Application updated: " + updatedApp))
                                .flatMap(updatedApp ->
                                    userService.getUserById(updatedApp.getUserId())
                                        .doOnSuccess(user -> log.info("User fetched for notification: " + user))
                                        .flatMap(user -> {
                                            String statusStr = "APPROVE".equals(action) ? "APROBADA" : "RECHAZADA";
                                            log.info("Sending status change notification. Status: " + statusStr + ". Email: " + user.getEmail());
                                            return queueSender.sendApplicationStatusChange(
                                                updatedApp,
                                                statusStr,
                                                user.getEmail()
                                            ).doOnSuccess(v -> log.info("Notification sent successfully"));
                                        })
                                );
                    })
                    .doOnError(e -> log.error("Error in actionApplication: " + e.getMessage() + e));
        }
        log.warn("Invalid action received: " + action);
        return Mono.error(new ValidationException(List.of("Invalid action")));
    }
}

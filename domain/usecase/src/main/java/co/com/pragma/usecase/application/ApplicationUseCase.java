package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.*;
import co.com.pragma.model.application.dto.PageResponse;
import co.com.pragma.usecase.application.exception.ApplicationNotFoundException;
import co.com.pragma.usecase.application.exception.ValidationException;
import co.com.pragma.model.mail.MailService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserService userService;
    private final ApplicationValidator validator;
    private final CatalogCachePort catalogCachePort;
    private final MailService mailService;

    public Mono<Application> saveApplication(Application application, String userIdentification) {
        return Mono.just(application)
                .doOnNext(app -> app.setStatus(catalogCachePort.getStatusIdByName("PENDIENTE")))
                .doOnNext(validator::validateFields)
                .flatMap(appVerified ->
                        loanTypeRepository.findById(appVerified.getType())
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
                .flatMap(applicationRepository::saveApplication);
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

    public Mono<Object> actionApplication(Long id, String action) {
        if ("APPROVE".equals(action) || "REJECT".equals(action)) {
            return getApplication(BigInteger.valueOf(id))
                    .flatMap(application -> {
                        if (!application.getStatus().equals(catalogCachePort.getStatusIdByName("PENDIENTE"))) {
                            return Mono.error(new ValidationException(List.of("Only applications with PENDIENTE status can be processed")));
                        }
                        Long newStatusId = "APPROVE".equals(action) ?
                                catalogCachePort.getStatusIdByName("APROBADA") :
                                catalogCachePort.getStatusIdByName("RECHAZADA");
                        application.setStatus(newStatusId);
                        return applicationRepository.updateApplication(application)
                                .flatMap(updatedApp ->
                                        userService.getUserById(updatedApp.getUserId())
                                                .flatMap(user -> {
                                                    String subject = "APPROVE".equals(action) ?
                                                            "Solicitud aprobada" : "Solicitud rechazada";
                                                    String body = "Hola " + user.getName() + ",\n\n" +
                                                            ("APPROVE".equals(action) ?
                                                                    "Tu solicitud ha sido aprobada." :
                                                                    "Tu solicitud ha sido rechazada.");
                                                    return mailService.sendMail(user.getEmail(), subject, body)
                                                            .onErrorResume(e -> Mono.empty()) // No fallar si el correo falla
                                                            .thenReturn(updatedApp);
                                                })
                                );
                    });
        } else {
            throw new ValidationException(List.of("Action must be either APPROVE or REJECT"));
        }
    }
}

package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.LoanType;
import co.com.pragma.model.application.gateways.*;
import co.com.pragma.usecase.application.exception.ValidationException;
import reactor.core.publisher.Mono;
import java.util.List;

public class ApplicationHelper {
    private ApplicationHelper() {}

    public static final String STATUS_PENDIENTE = "Pendiente";
    public static final String STATUS_APROBADA = "Aprobado";
    public static final String STATUS_RECHAZADA = "Rechazada";

    public static Mono<Application> validateLoanTypeAndAmount(Application app, CatalogCachePort catalogCachePort, ApplicationValidator validator) {
        return catalogCachePort.getLoanTypeById(app.getType())
                .switchIfEmpty(Mono.error(new ValidationException(List.of("Loan Type not found"))))
                .map(type -> {
                    validator.validateAmount(app, type);
                    return app;
                });
    }

    public static Mono<Application> assignUserIdToApplication(Application app, String userIdentification, UserService userService) {
        return userService.getUserByIdentification(userIdentification)
                .switchIfEmpty(Mono.error(new ValidationException(List.of("User not found"))))
                .map(user -> {
                    app.setUserId(user.getId());
                    return app;
                });
    }

    public static Mono<Application> handleAutomaticValidation(Application savedApp, CatalogCachePort catalogCachePort, ApplicationRepository applicationRepository, QueueSender queueSender, LogPort log, UserService userService) {
        return catalogCachePort.getLoanTypeById(savedApp.getType())
            .flatMap(loanType -> {
                if (Boolean.TRUE.equals(loanType.getAutoValidation())) {
                    return userService.getUserById(savedApp.getUserId())
                        .flatMap(user ->
                            applicationRepository.findAllByUserIdAndStatus(
                                savedApp.getUserId(),
                                catalogCachePort.getStatusIdByName(STATUS_APROBADA)
                            )
                            .collectList()
                            .flatMap(approvedApps ->
                                catalogCachePort.getAllLoanTypes()
                                    .collectMap(LoanType::getId, LoanType::getInterestRate)
                                    .flatMap(loanTypeRateMap ->
                                        queueSender.sendApplicationForAutomaticValidation(savedApp, approvedApps, user, loanTypeRateMap)
                                            .thenReturn(savedApp)
                                    )
                            )
                            .onErrorResume(e -> {
                                log.error("Error fetching approved applications for userId: " + savedApp.getUserId() + e);
                                return Mono.just(savedApp);
                            })
                        );
                } else {
                    return Mono.just(savedApp);
                }
            });
    }

    public static boolean isValidAction(String action) {
        return "APPROVE".equals(action) || "REJECT".equals(action);
    }

    public static Mono<Application> validatePendingStatus(Application application, CatalogCachePort catalogCachePort, LogPort log) {
        Long pendienteStatus = catalogCachePort.getStatusIdByName(STATUS_PENDIENTE);
        if (!application.getStatus().equals(pendienteStatus)) {
            log.warn("Application status is not PENDIENTE. Current status: " + application.getStatus());
            return Mono.error(new ValidationException(List.of("Only applications with PENDIENTE status can be processed")));
        }
        return Mono.just(application);
    }

    public static Mono<Void> updateStatusAndNotify(Application application, String action, CatalogCachePort catalogCachePort, ApplicationRepository applicationRepository, UserService userService, QueueSender queueSender, LogPort log) {
        Long newStatusId = "APPROVE".equals(action) ?
                catalogCachePort.getStatusIdByName(STATUS_APROBADA) :
                catalogCachePort.getStatusIdByName(STATUS_RECHAZADA);
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
    }
}

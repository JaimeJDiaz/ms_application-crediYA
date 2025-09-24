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

import static co.com.pragma.usecase.application.ApplicationHelper.*;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final ApplicationValidator validator;
    private final CatalogCachePort catalogCachePort;
    private final QueueSender queueSender;
    private final LogPort log;

    public Mono<Application> saveApplication(Application application, String userIdentification) {
        return Mono.just(application)
                .doOnNext(app -> app.setStatus(catalogCachePort.getStatusIdByName(STATUS_PENDIENTE)))
                .doOnNext(validator::validateFields)
                .flatMap(app -> ApplicationHelper.validateLoanTypeAndAmount(app, catalogCachePort, validator))
                .flatMap(appVerified -> ApplicationHelper.assignUserIdToApplication(appVerified, userIdentification, userService))
                .flatMap(applicationRepository::saveApplication)
                .flatMap(app -> ApplicationHelper.handleAutomaticValidation(app, catalogCachePort, applicationRepository, queueSender, log, userService));
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
        if (!ApplicationHelper.isValidAction(action)) {
            log.warn("Invalid action received: " + action);
            return Mono.error(new ValidationException(List.of("Invalid action")));
        }
        return getApplication(BigInteger.valueOf(id))
                .doOnSubscribe(sub -> log.info("Fetching application with id: " + id))
                .flatMap(app -> ApplicationHelper.validatePendingStatus(app, catalogCachePort, log))
                .flatMap(app -> ApplicationHelper.updateStatusAndNotify(app, action, catalogCachePort, applicationRepository, userService, queueSender, log))
                .doOnError(e -> log.error("Error in actionApplication: " + e.getMessage() + e));
    }
}

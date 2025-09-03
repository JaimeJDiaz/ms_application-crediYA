package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.application.gateways.LogPort;
import co.com.pragma.usecase.application.exception.ApplicationNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.pragma.model.application.enums.Status.*;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final ApplicationValidator applicationValidator;
    private final LogPort log;

    public Mono<Application> saveApplication(Application application) {
        return Mono.just(application)
                .doOnNext(app -> app.setStatus(PENDING_REVIEW))
                .doOnNext(applicationValidator::validateApplication)
                .doOnNext(app -> log.debug("APPLICATION_VALIDATION_PASSED: {}"))
                .flatMap(applicationRepository::saveApplication)
                .doOnSuccess(savedApp -> log.info("APPLICATION_SAVED_SUCCESSFULLY: {}"))
                .doOnError(error -> log.error("APPLICATION_SAVE_FAILED: {}"));
    }

    public Mono<Application> updateApplication(String id, Application application) {
        return applicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationNotFoundException("Application not found with ID: " + id)))
                .doOnNext(existingApp -> {
                    existingApp.setAmount(application.getAmount());
                    existingApp.setType(application.getType());
                    existingApp.setStatus(application.getStatus());
                })
                .doOnNext(applicationValidator::validateApplication)
                .doOnNext(app -> log.debug("APPLICATION_VALIDATION_PASSED for update: {}"))
                .flatMap(applicationRepository::saveApplication)
                .doOnSuccess(updatedApp -> log.info("APPLICATION_UPDATED_SUCCESSFULLY: {}"))
                .doOnError(error -> log.error("APPLICATION_UPDATE_FAILED: {}"));
    }
}

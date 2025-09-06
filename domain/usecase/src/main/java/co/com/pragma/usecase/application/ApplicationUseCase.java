package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.LoanType;
import co.com.pragma.model.application.Status;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.application.gateways.LoanTypeRepository;
import co.com.pragma.model.application.gateways.LogPort;
import co.com.pragma.model.application.gateways.StatusRepository;
import co.com.pragma.usecase.application.exception.ApplicationNotFoundException;
import co.com.pragma.usecase.application.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@RequiredArgsConstructor
public class ApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final ApplicationValidator validator;
    private final LogPort log;

    public Mono<Application> saveApplication(Application application) {
        return Mono.just(application)
                .doOnNext(app -> app.setStatus(1))
                .doOnNext(validator::validateFields)
                .flatMap(appVerified ->
                        loanTypeRepository.findById(appVerified.getType())
                                .switchIfEmpty(Mono.error(new ValidationException(List.of("Loan Type not found"))))
                                .map(type -> {
                                    validator.validateAmount(appVerified, type);
                                    return appVerified;
                                })
                )
                .flatMap(applicationRepository::saveApplication);
    }

    /*public Mono<Application> updateApplication(BigInteger id, Application application) {
        return applicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationNotFoundException("Application not found with ID: " + id)))
                .doOnNext(existingApp -> {
                    existingApp.setAmount(application.getAmount());
                    existingApp.setType(application.getType());
                    existingApp.setStatus(application.getStatus());
                })
                //.doOnNext(applicationValidator::validateApplication)
                .doOnNext(app -> log.debug("APPLICATION_VALIDATION_PASSED for update: {}"))
                .flatMap(applicationRepository::saveApplication)
                .doOnSuccess(updatedApp -> log.info("APPLICATION_UPDATED_SUCCESSFULLY: {}"))
                .doOnError(error -> log.error("APPLICATION_UPDATE_FAILED: {}"));
    }*/

    public Mono<Application> getApplication(BigInteger id) {
        if (id == null) throw new ValidationException(List.of("Id is required"));
        return applicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationNotFoundException("ERROR_FETCHING_USER_BY_ID")));

    }
}

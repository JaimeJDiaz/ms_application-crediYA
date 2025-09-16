package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.*;
import co.com.pragma.model.dto.PageResponse;
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
    private final UserService userService;
    private final ApplicationValidator validator;
    private final CatalogCachePort catalogCachePort;

    public Mono<Application> saveApplication(Application application, String userIdentification) {
        return Mono.just(application)
                .doOnNext(app -> app.setStatus(1L))
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
}

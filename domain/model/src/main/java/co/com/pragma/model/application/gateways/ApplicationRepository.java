package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.dto.PageResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface ApplicationRepository {

    Mono<Application> saveApplication(Application application);

    Mono<Application> updateApplication(Application application);

    Mono<Application> findById(BigInteger id);

    Mono<PageResponse<Application>> findAll(Integer page, Integer size, Long statusId);
    Flux<Application> findAllByUserIdAndStatus(BigInteger userId, Long statusId);
}
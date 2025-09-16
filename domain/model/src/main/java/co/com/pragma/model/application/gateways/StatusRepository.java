package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StatusRepository {
    Mono<Status> findById(Long id);
    Mono<Status> findByName(String name);
    Flux<Status> findAll();
}

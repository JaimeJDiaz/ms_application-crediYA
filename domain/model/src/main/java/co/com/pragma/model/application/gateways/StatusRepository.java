package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.Status;
import reactor.core.publisher.Mono;

public interface StatusRepository {
    Mono<Status> findById(Long id);
}

package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.LoanType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {

    Mono<LoanType> findById(Long id);

    Flux<LoanType> findAll();
}

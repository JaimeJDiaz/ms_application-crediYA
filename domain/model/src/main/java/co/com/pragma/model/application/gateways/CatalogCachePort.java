package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.LoanType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CatalogCachePort {
    Long getStatusIdByName(String name);
    Long getLoanTypeIdByName(String name);
    Mono<LoanType> getLoanTypeById(Long id);
    Flux<LoanType> getAllLoanTypes();
}


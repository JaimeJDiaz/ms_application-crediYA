package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entities.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveLoanTypeRepository extends ReactiveCrudRepository<LoanTypeEntity, Integer>, ReactiveQueryByExampleExecutor<LoanTypeEntity> {
}

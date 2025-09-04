package co.com.pragma.r2dbc;

import co.com.pragma.model.application.LoanType;
import co.com.pragma.model.application.gateways.LoanTypeRepository;
import co.com.pragma.r2dbc.entities.LoanTypeEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ReactiveLoanTypeRepositoryAdapter extends ReactiveAdapterOperations<LoanType, LoanTypeEntity, Integer, ReactiveLoanTypeRepository> implements LoanTypeRepository {
    protected ReactiveLoanTypeRepositoryAdapter(ReactiveLoanTypeRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, LoanType.class));
    }

    @Override
    public Mono<LoanType> findById(Integer id) {
        return super.findById(id)
                .map(entity -> mapper.map(entity, LoanType.class));
    }
}

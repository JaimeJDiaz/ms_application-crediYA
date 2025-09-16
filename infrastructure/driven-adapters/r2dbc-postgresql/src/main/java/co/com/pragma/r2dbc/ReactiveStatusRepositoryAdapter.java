package co.com.pragma.r2dbc;

import co.com.pragma.model.application.Status;
import co.com.pragma.model.application.gateways.StatusRepository;
import co.com.pragma.r2dbc.entities.StatusEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ReactiveStatusRepositoryAdapter extends ReactiveAdapterOperations<Status, StatusEntity, Long, ReactiveStatusRepository> implements StatusRepository {
    protected ReactiveStatusRepositoryAdapter(ReactiveStatusRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Status.class));
    }

    @Override
    public Mono<Status> findById(Long id) {
        return super.findById(id)
                .map(entity -> mapper.map(entity, Status.class));
    }

    @Override
    public Mono<Status> findByName(String name) {
        return super.repository.findByName(name)
                .map(entity -> mapper.map(entity, Status.class));
    }

}
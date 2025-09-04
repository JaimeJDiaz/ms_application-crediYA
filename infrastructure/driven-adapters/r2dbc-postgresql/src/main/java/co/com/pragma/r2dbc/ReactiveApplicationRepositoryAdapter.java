package co.com.pragma.r2dbc;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.r2dbc.entities.ApplicationEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class ReactiveApplicationRepositoryAdapter extends ReactiveAdapterOperations<Application, ApplicationEntity, BigInteger, ReactiveApplicationRepository> implements ApplicationRepository {
    public ReactiveApplicationRepositoryAdapter(ReactiveApplicationRepository repository, ObjectMapper mapper) {

        super(repository, mapper, entity -> mapper.map(entity, Application.class));
    }

    @Override
    public Mono<Application> saveApplication(Application application) {
        return super.save(application);
    }

    @Override
    public Mono<Application> updateApplication(Application application) {
        return super.save(application);
    }

    @Override
    public Mono<Application> findById(BigInteger id) {
        return super.repository.findById(id)
                .map(entity -> mapper.map(entity, Application.class));
    }
}

package co.com.pragma.r2dbc;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.application.dto.PageResponse;
import co.com.pragma.r2dbc.entities.ApplicationEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
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

    @Override
    public Mono<PageResponse<Application>> findAll(Integer page, Integer size, Long statusId) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 20;
        long offset = (long) pageNumber * pageSize;

        Mono<Long> totalElementsMono = super.repository.countByStatusId(statusId);
        Mono<java.util.List<Application>> contentMono = super.repository.findByStatusIdPaged(statusId, offset, pageSize)
                .map(entity -> mapper.map(entity, Application.class))
                .collectList();

        return totalElementsMono.zipWith(contentMono)
                .map(tuple -> {
                    long totalElements = tuple.getT1();
                    java.util.List<Application> content = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / pageSize);
                    return PageResponse.<Application>builder()
                            .content(content)
                            .page(pageNumber)
                            .size(pageSize)
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                            .build();
                });
    }

    @Override
    public Flux<Application> findAllByUserIdAndStatus(BigInteger userId, Long statusId) {
        return super.repository.findAllByUserIdAndStatus(userId, statusId)
                .map(entity -> mapper.map(entity, Application.class));

    }
}

package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entities.ApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface ReactiveApplicationRepository extends ReactiveCrudRepository<ApplicationEntity, BigInteger>, ReactiveQueryByExampleExecutor<ApplicationEntity> {
    @Query("SELECT COUNT(*) FROM applications WHERE (:statusId IS NULL OR status_id = :statusId)")
    Mono<Long> countByStatusId(@Param("statusId") Long statusId);

    @Query("SELECT * FROM applications WHERE (:statusId IS NULL OR status_id = :statusId) OFFSET :offset LIMIT :limit")
    Flux<ApplicationEntity> findByStatusIdPaged(@Param("statusId") Long statusId, @Param("offset") long offset, @Param("limit") int limit);

    Flux<ApplicationEntity> findAllByUserIdAndStatus(BigInteger userId, Long statusId);
}

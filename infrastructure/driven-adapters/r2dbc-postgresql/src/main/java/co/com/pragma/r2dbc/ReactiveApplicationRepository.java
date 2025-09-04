package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entities.ApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface ReactiveApplicationRepository extends ReactiveCrudRepository<ApplicationEntity, BigInteger>, ReactiveQueryByExampleExecutor<ApplicationEntity> {

}

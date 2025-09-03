package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.ApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface MyReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, BigInteger>, ReactiveQueryByExampleExecutor<ApplicationEntity> {

}

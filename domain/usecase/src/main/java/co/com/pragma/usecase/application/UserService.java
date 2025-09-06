package co.com.pragma.usecase.application;

import co.com.pragma.model.application.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> getUserByIdentification(String identification);
}


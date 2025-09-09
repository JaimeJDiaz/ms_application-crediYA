package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> getUserByIdentification(String identification);
}


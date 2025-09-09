package co.com.pragma.consumer.adapters;

import co.com.pragma.consumer.RestConsumer;
import co.com.pragma.consumer.mappers.UserMapper;
import co.com.pragma.model.application.User;
import co.com.pragma.model.application.gateways.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserAdapter implements UserService {

    private final RestConsumer restConsumer;
    private final UserMapper userMapper;

    @Override
    public Mono<User> getUserByIdentification(String identification) {
        return restConsumer.getUserByIdentification(identification)
                .map(userMapper::toModel);
    }
}

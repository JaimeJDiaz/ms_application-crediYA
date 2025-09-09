package co.com.pragma.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class RestConsumer {
    private final WebClient client;

    public Mono<UserDto> getUserByIdentification(String identification) {
        return client
                .get()
                .uri("/documento/" + identification)
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}

package co.com.pragma.consumer;


import co.com.pragma.consumer.jwt.InternalTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class RestConsumer {

    private final WebClient client;
    private final InternalTokenService internalTokenService;
    public Mono<UserDto> getUserByIdentification(String identification) {
        String token = internalTokenService.getInternalToken();
        return client
                .get()
                .uri("/identificacion/" + identification)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        response.bodyToMono(String.class).flatMap(errorBody -> {
                            System.err.println("Error al consumir el servicio: " + response.statusCode() + " - " + errorBody);
                            return Mono.error(new RuntimeException("Error al consumir el servicio: " + response.statusCode() + " - " + errorBody));
                        })
                )
                .bodyToMono(UserDto.class);
    }
}

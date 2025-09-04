package co.com.pragma.api;

import co.com.pragma.model.application.Application;
import co.com.pragma.usecase.application.ApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class Handler {
    private final ApplicationUseCase useCase;
    private final TransactionalOperator operator;

    public Mono<ServerResponse> listenSaveApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Application.class)
                .flatMap(application -> operator.transactional(useCase.saveApplication(application)))
                .flatMap(savedApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedApplication));
    }

    public Mono<ServerResponse> listenUpdateApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Application.class)
                .flatMap(application -> operator.transactional(useCase.updateApplication(application.getId(), application)))
                .flatMap(updatedApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedApplication));
    }

    public Mono<ServerResponse> listenGetApplication(ServerRequest serverRequest) {
        try {
            BigInteger applicationId = new BigInteger(serverRequest.pathVariable("id"));
            return operator.transactional(useCase.getApplication(applicationId))
                    .flatMap(application -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(application))
                    .switchIfEmpty(ServerResponse.notFound().build());
        } catch (NumberFormatException e) {
            return ServerResponse.badRequest().bodyValue("ID inválid");
        }
    }

}

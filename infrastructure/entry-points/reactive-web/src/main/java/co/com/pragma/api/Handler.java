package co.com.pragma.api;

import co.com.pragma.api.dto.ActionApplicationDto;
import co.com.pragma.api.dto.CreateApplicationDto;
import co.com.pragma.model.application.Application;
import co.com.pragma.usecase.application.ApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class Handler {
    private final ApplicationUseCase useCase;
    private final TransactionalOperator operator;

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public Mono<ServerResponse> listenSaveApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateApplicationDto.class)
                .flatMap(dto -> {
                    Application application = Application.builder()
                            .amount(dto.amount())
                            .type(dto.type() != null ? dto.type().longValue() : null)
                            .term(dto.term())
                            .build();
                    String userIdentification = dto.userIdentification();
                    return operator.transactional(useCase.saveApplication(application, userIdentification));
                })
                .flatMap(savedApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedApplication));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Mono<ServerResponse> listenFindApplications(ServerRequest serverRequest) {
        Integer page = serverRequest.queryParam("page").map(Integer::parseInt).orElse(0);
        Integer size = serverRequest.queryParam("size").map(Integer::parseInt).orElse(20);
        String status = serverRequest.queryParam("status").orElse(null);
        return useCase.findApplications(page, size, status)
                .flatMap(applications -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(applications));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Mono<ServerResponse> listenActionApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ActionApplicationDto.class)
                .flatMap(dto -> operator.transactional(useCase.actionApplication(dto.id(), dto.action())))
                .flatMap(updatedApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedApplication));
    }


}

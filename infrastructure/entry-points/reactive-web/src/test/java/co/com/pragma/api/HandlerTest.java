package co.com.pragma.api;

import co.com.pragma.api.dto.CreateApplicationDto;
import co.com.pragma.model.application.Application;
import co.com.pragma.usecase.application.ApplicationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HandlerTest {
    @Mock
    private ApplicationUseCase useCase;
    @Mock
    private TransactionalOperator operator;
    private Handler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new Handler(useCase, operator);
    }

    @Test
    void listenSaveApplication_success() {
        // Arrange
        CreateApplicationDto dto = new CreateApplicationDto(BigDecimal.valueOf(1000), 1, "123456789", 12);
        Application savedApp = Application.builder().amount(BigDecimal.valueOf(1000)).type(1L).term(12).userId(null).build();
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(CreateApplicationDto.class)).thenReturn(Mono.just(dto));
        when(operator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(useCase.saveApplication(any(Application.class), any(String.class))).thenReturn(Mono.just(savedApp));

        // Act
        Mono<ServerResponse> responseMono = handler.listenSaveApplication(request);

        // Assert
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    MediaType contentType = response.headers().getContentType();
                    assertNotNull(contentType);
                    assertEquals(MediaType.APPLICATION_JSON, contentType);
                })
                .verifyComplete();

        ArgumentCaptor<Application> appCaptor = ArgumentCaptor.forClass(Application.class);
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(useCase).saveApplication(appCaptor.capture(), idCaptor.capture());
        assertEquals(dto.amount(), appCaptor.getValue().getAmount());
        assertEquals(dto.type().longValue(), appCaptor.getValue().getType());
        assertEquals(dto.term(), appCaptor.getValue().getTerm());
        assertEquals(dto.userIdentification(), idCaptor.getValue());
    }

    @Test
    void listenSaveApplication_error() {
        // Arrange
        CreateApplicationDto dto = new CreateApplicationDto(BigDecimal.valueOf(1000), 1, "123456789", 12);
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(CreateApplicationDto.class)).thenReturn(Mono.just(dto));
        when(operator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(useCase.saveApplication(any(Application.class), any(String.class))).thenReturn(Mono.error(new RuntimeException("error")));

        // Act
        Mono<ServerResponse> responseMono = handler.listenSaveApplication(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectError(RuntimeException.class)
                .verify();
    }
}
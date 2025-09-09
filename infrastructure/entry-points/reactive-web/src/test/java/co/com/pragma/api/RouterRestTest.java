package co.com.pragma.api;

import co.com.pragma.model.application.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class})
@Import(RouterRestTest.HandlerMockConfig.class)
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private Handler handler;

    private Application mockApp;

    @TestConfiguration
    static class HandlerMockConfig {
        @Bean
        public Handler handler() {
            return Mockito.mock(Handler.class);
        }
    }

    @BeforeEach
    void setUp() {
        mockApp = Application.builder()
                .amount(BigDecimal.valueOf(1000))
                .type(1L)
                .term(12)
                .userId(null)
                .build();
    }

    @Test
    void testPostSolicitud_success() {
        when(handler.listenSaveApplication(any())).thenReturn(
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(mockApp)
        );

        String json = "{" +
                "\"amount\":1000," +
                "\"type\":1," +
                "\"userIdentification\":\"123456789\"," +
                "\"term\":12" +
                "}";

        webTestClient.post()
                .uri("/api/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.amount").isEqualTo(1000)
                .jsonPath("$.type").isEqualTo(1)
                .jsonPath("$.term").isEqualTo(12);
    }

    @Test
    void testPostSolicitud_error() {
        when(handler.listenSaveApplication(any())).thenReturn(ServerResponse.badRequest().bodyValue("error"));

        String json = "{" +
                "\"amount\":1000," +
                "\"type\":1," +
                "\"userIdentification\":\"123456789\"," +
                "\"term\":12" +
                "}";

        webTestClient.post()
                .uri("/api/v1/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("error");
    }
}

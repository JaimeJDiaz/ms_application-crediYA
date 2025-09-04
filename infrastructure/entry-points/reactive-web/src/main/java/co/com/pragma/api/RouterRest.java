package co.com.pragma.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                .POST("/api/v1/solicitudes", handler::listenSaveApplication)
                .POST("/api/v1/solicitudes/update", handler::listenUpdateApplication)
                .GET("/api/v1/solicitudes/{id}", handler::listenGetApplication)
                .build();

    }
}

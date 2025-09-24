package co.com.pragma.api;

import co.com.pragma.model.application.Application;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @RouterOperations({
            @RouterOperation(path = "/api/v1/solicitudes", produces = "application/json", method = RequestMethod.POST,
                    beanClass = Handler.class, beanMethod = "listenSaveApplication",
                    operation = @Operation(
                            operationId = "save",
                            summary = "Guarda un nueva solicitud",
                            tags = {"Solicitudes"},
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = Application.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Aplicacion guardado exitosamente",
                                            content = @Content(schema = @Schema(implementation = Application.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
                            }
                    )
            ),
            @RouterOperation(path = "/api/v1/solicitudes", produces = "application/json", method = RequestMethod.GET,
                    beanClass = Handler.class, beanMethod = "listenFindApplications",
                    operation = @Operation(
                            operationId = "findApplications",
                            summary = "Consulta solicitudes con paginación y filtro por estado",
                            tags = {"Solicitudes"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista paginada de aplicaciones",
                                            content = @Content(schema = @Schema(implementation = Application.class)))
                            }
                    )
            ),
            @RouterOperation(path = "/api/v1/solicitudes/action", produces = "application/json", method = RequestMethod.PUT,
                    beanClass = Handler.class, beanMethod = "listenActionApplication",
                    operation = @Operation(
                            operationId = "actionApplication",
                            summary = "Aprueba o rechaza una solicitud",
                            tags = {"Solicitudes"},
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = co.com.pragma.api.dto.ActionApplicationDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Solicitud procesada exitosamente",
                                            content = @Content(schema = @Schema(implementation = co.com.pragma.model.application.Application.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                .POST("/api/v1/solicitudes", handler::listenSaveApplication)
                .GET("/api/v1/solicitudes", handler::listenFindApplications)
                .PUT("/api/v1/solicitudes", handler::listenActionApplication)
                .build();

    }
}

package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import io.swagger.v3.oas.annotations.Operation;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/usuarios",
            beanClass = Handler.class,
            beanMethod = "createUser",
            method = RequestMethod.POST,
            operation = @Operation(
                operationId = "createUser",
                tags = {"User"},
                summary = "Create a new user",
                description = "Creates a new user in the system",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User to create",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = co.com.bancolombia.api.dto.CreateUserDTO.class)
                    )
                ),
                responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data", content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = co.com.bancolombia.api.dto.ErrorResponse.class)
                    )),
                }
        ))
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::createUser);
    }
}

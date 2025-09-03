package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

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
        )),
        @RouterOperation(
            path = "/api/v1/login",
            beanClass = Handler.class,
            beanMethod = "authenticate",
            method = RequestMethod.POST,
            operation = @Operation(
                operationId = "authenticate",
                tags = {"User"},
                summary = "Login a user",
                description = "Authenticates a user and returns a token",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = co.com.bancolombia.api.dto.LoginUserDTO.class)
                    )
                ),
                responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User authenticated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication failed", content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = co.com.bancolombia.api.dto.ErrorResponse.class)
                    ))
                }
        )),
        @RouterOperation(
            path = "/api/v1/usuarios/{id}",
            beanClass = Handler.class,
            beanMethod = "getUser",
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getUser",
                tags = {"User"},
                summary = "Get a user by ID",
                description = "Retrieve a user in the system by ID",
                parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                        name = "id",
                        in = ParameterIn.PATH,
                        required = true,
                        description = "ID of the user",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string")
                    )
                },
                responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "User found successfully",
                        content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = co.com.bancolombia.api.dto.FindUserDTO.class)
                        )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = co.com.bancolombia.api.dto.ErrorResponse.class)
                        )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "User not found"
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/usuarios",
            beanClass = Handler.class,
            beanMethod = "getUser",
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getUser",
                tags = {"User"},
                summary = "Get a user by document",
                description = "Retrieve a user in the system by document number",
                parameters = {
                    @Parameter(
                        name = "document",
                        in = ParameterIn.QUERY,
                        required = true,
                        description = "Document number of the user",
                        schema = @Schema(type = "string")
                    )
                },
                responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "User found successfully",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = co.com.bancolombia.api.dto.FindUserDTO.class)
                        )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = co.com.bancolombia.api.dto.ErrorResponse.class)
                        )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "User not found"
                    )
                }
            )
        )        
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::createUser).
                andRoute(POST("/api/v1/login"), handler::authenticate).
                andRoute(GET("/api/v1/usuarios/{id}"), handler::getUserById).
                andRoute(GET("/api/v1/usuarios"), handler::getUserByDocument);
    }
}

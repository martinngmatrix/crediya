package co.com.bancolombia.api;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.api.dto.CreateUserDTO;
import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.api.mapper.UserDTOMapper;
import co.com.bancolombia.api.validation.ValidationService;
import co.com.bancolombia.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
private  final UserUseCase userUseCase;
private final UserDTOMapper mapper;
private final ValidationService validationService;
private static final Logger log = LoggerFactory.getLogger(Handler.class);
//private  final UseCase2 useCase2;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        log.trace("Inicio del proceso de creación de usuario");
        return serverRequest.bodyToMono(CreateUserDTO.class)
                .flatMap(validationService::validate)
                .map(mapper::toModel)
                .flatMap(userUseCase::createUser)
                .doOnSuccess(user -> log.info("Usuario creado con exito"))
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(e -> {
                    ErrorResponse error = new ErrorResponse(
                            e.getMessage() != null ? e.getMessage() : "Error inesperado",
                            400
                    );
                    return ServerResponse.status(400).bodyValue(error);
                });
    } 
}

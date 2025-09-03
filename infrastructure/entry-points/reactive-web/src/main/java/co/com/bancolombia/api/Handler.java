package co.com.bancolombia.api;

import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.bancolombia.api.constants.Constants;
import co.com.bancolombia.api.constants.messages.ApiResponseMessages;
import co.com.bancolombia.api.dto.CreateUserDTO;
import co.com.bancolombia.api.dto.ErrorResponse;
import co.com.bancolombia.api.dto.LoginUserDTO;
import co.com.bancolombia.api.mapper.UserDTOMapper;
import co.com.bancolombia.api.validation.ValidationService;
import co.com.bancolombia.model.user.constants.messages.UserErrorMessages;
import co.com.bancolombia.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
private  final UserUseCase userUseCase;
private final UserDTOMapper userMapper;
private final ValidationService validationService;
private static final Logger log = LoggerFactory.getLogger(Handler.class);

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        log.trace(ApiResponseMessages.CREATE_USER_REQUEST_RECEIVED);
        return serverRequest.bodyToMono(CreateUserDTO.class)
                .flatMap(validationService::validate)
                .map(userMapper::toModel)
                .flatMap(userUseCase::createUser)
                .doOnSuccess(user -> log.info(ApiResponseMessages.USER_CREATED))
                .then(ServerResponse
                .status(HttpStatus.CREATED)
                .build())
                .onErrorResume(e -> {
                    ErrorResponse error = new ErrorResponse(
                            e.getMessage() != null ? e.getMessage() : Constants.UNEXPECTED_ERROR
                    );
                    return ServerResponse.status(400).bodyValue(error);
                });
    }

    public Mono<ServerResponse> authenticate(ServerRequest serverRequest) {
        log.trace(ApiResponseMessages.AUTHENTICATE_REQUEST_RECEIVED);
        return serverRequest.bodyToMono(LoginUserDTO.class)
                .flatMap(validationService::validate)
                .flatMap(dto -> 
                    {
                        String email = dto.email();
                        String password = dto.password();
                        return userUseCase.authenticate(email, password);
                    }
                )
                .doOnSuccess(token -> log.info(ApiResponseMessages.USER_AUTHENTICATED))
                .flatMap(token -> {
                    Map<String, String> responseBody = Map.of("token", token);
                    return ServerResponse
                        .status(HttpStatus.OK)
                        .bodyValue(responseBody);
                    })
                .onErrorResume(e -> {
                    ErrorResponse error = new ErrorResponse(
                            e.getMessage() != null ? e.getMessage() : Constants.UNEXPECTED_ERROR
                    );
                    return ServerResponse.status(400).bodyValue(error);
                });
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        log.trace(ApiResponseMessages.FIND_BY_ID_REQUEST_RECEIVED);
        BigInteger id = new BigInteger(serverRequest.pathVariable("id"));
        return userUseCase.findById(id)
        .map(userMapper::toFindUserDTO)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .onErrorResume(e -> {
                if (UserErrorMessages.USER_NOT_FOUND.equals(e.getMessage())) {
                    ErrorResponse error = new ErrorResponse(e.getMessage());
                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(error);
                }
                ErrorResponse error = new ErrorResponse("Error inesperado");
                return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(error);
            });
    }

    public Mono<ServerResponse> getUserByDocument(ServerRequest serverRequest) {
        log.trace(ApiResponseMessages.FIND_BY_DOCUMENT_REQUEST_RECEIVED);
        String document = serverRequest.queryParam("document").orElse("");
        return userUseCase.findByDocumentNumber(document)
            .map(userMapper::toFindUserDTO)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .onErrorResume(e -> {
                if (UserErrorMessages.USER_NOT_FOUND.equals(e.getMessage())) {
                    ErrorResponse error = new ErrorResponse(e.getMessage());
                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(error);
                }
                ErrorResponse error = new ErrorResponse("Error inesperado");
                return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(error);
            });
    }
}

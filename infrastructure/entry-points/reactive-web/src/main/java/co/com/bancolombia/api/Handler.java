package co.com.bancolombia.api;

import lombok.RequiredArgsConstructor;

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
import co.com.bancolombia.api.mapper.UserDTOMapper;
import co.com.bancolombia.api.validation.ValidationService;
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
}

package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.constants.messages.UserErrorMessages;
import co.com.bancolombia.model.user.gateways.JwtService;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Mono<Void> createUser(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> Mono.<Void>error(new IllegalArgumentException(UserErrorMessages.EMAIL_ALREADY_EXISTS)))
                .switchIfEmpty(userRepository.createUser(user));
    }

    public Mono<String> authenticate(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(UserErrorMessages.INVALID_CREDENTIALS)))
                .map(user -> jwtService.generateToken(user));
    }
}

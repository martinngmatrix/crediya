package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;

    public Mono<Void> createUser(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> Mono.<Void>error(new IllegalArgumentException("Correo ya registrado")))
                .switchIfEmpty(userRepository.createUser(user));
    }
}

package co.com.bancolombia.model.user.gateways;

import java.math.BigInteger;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<Void> createUser(User user);
    Mono<User> findByEmail(String email);
    Mono<User> findByDocumentNumber(String documentNumber);
    Mono<User> findById(BigInteger id);
    Mono<User> findByEmailAndPassword(String email, String password);
}

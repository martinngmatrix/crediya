package co.com.bancolombia.usecase.user;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.utils.JwtService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;    

    @InjectMocks
    private UserUseCase userUseCase;
    
    private User user;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .name("Test User")
                .build();
    }

    @Test
    void createUserShouldCreateUserWhenEmailNotExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());
        when(userRepository.createUser(user)).thenReturn(Mono.empty());

        Mono<Void> result = userUseCase.createUser(user);

        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).createUser(user);
    }

    @Test
    void createUserShouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(mock(User.class)));
        when(userRepository.createUser(user)).thenReturn(Mono.empty());
        
        Mono<Void> result = userUseCase.createUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals("Correo ya registrado"))
                .verify();
    }

    @Test
    void createUserShouldPropagateErrorFromCreateUser() {
        RuntimeException exception = new RuntimeException("Failed to create user");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());
        when(userRepository.createUser(user)).thenReturn(Mono.error(exception));

        Mono<Void> result = userUseCase.createUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().equals("Failed to create user"))
                .verify();

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).createUser(user);
    }

    @Test
    void authenticateShouldReturnTokenWhenCredentialsAreValid() {
        String email = "test@example.com";
        String password = "password123";
        String expectedToken = "jwt-token";

        when(userRepository.findByEmailAndPassword(email, password)).thenReturn(Mono.just(user));
        when(jwtService.generateToken(user)).thenReturn(Mono.just(expectedToken)); // <-- CORRECCIÓN

        Mono<String> result = userUseCase.authenticate(email, password);

        StepVerifier.create(result)
                .expectNext(expectedToken)
                .verifyComplete();

        verify(userRepository, times(1)).findByEmailAndPassword(email, password);
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void authenticateShouldThrowExceptionWhenCredentialsAreInvalid() {
        String email = "wrong@example.com";
        String password = "wrongpass";

        when(userRepository.findByEmailAndPassword(email, password)).thenReturn(Mono.empty());

        Mono<String> result = userUseCase.authenticate(email, password);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Credenciales inválidas"))
                .verify();

        verify(userRepository, times(1)).findByEmailAndPassword(email, password);
    }

    @Test
    void findByIdShouldReturnUserWhenFound() {
        BigInteger id = BigInteger.ONE;

        when(userRepository.findById(id)).thenReturn(Mono.just(user));

        Mono<User> result = userUseCase.findById(id);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void findByIdShouldThrowExceptionWhenNotFound() {
        BigInteger id = BigInteger.TEN;

        when(userRepository.findById(id)).thenReturn(Mono.empty());

        Mono<User> result = userUseCase.findById(id);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Usuario no encontrado"))
                .verify();

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void findByDocumentNumberShouldReturnUserWhenFound() {
        String documentNumber = "12345678";

        when(userRepository.findByDocumentNumber(documentNumber)).thenReturn(Mono.just(user));

        Mono<User> result = userUseCase.findByDocumentNumber(documentNumber);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

        verify(userRepository, times(1)).findByDocumentNumber(documentNumber);
    }

    @Test
    void findByDocumentNumberShouldThrowExceptionWhenNotFound() {
        String documentNumber = "99999999";

        when(userRepository.findByDocumentNumber(documentNumber)).thenReturn(Mono.empty());

        Mono<User> result = userUseCase.findByDocumentNumber(documentNumber);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Usuario no encontrado"))
                .verify();

        verify(userRepository, times(1)).findByDocumentNumber(documentNumber);
    }
}
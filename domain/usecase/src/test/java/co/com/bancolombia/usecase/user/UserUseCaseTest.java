package co.com.bancolombia.usecase.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseTest {
    
    @Mock
    private UserRepository userRepository;
    
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

}
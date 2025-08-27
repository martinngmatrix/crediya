package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .email("john.doe@example.com")
                .phone("1234567890")
                .baseSalary(new BigDecimal("50000.00"))
                .build();

        userEntity = new UserEntity();
        userEntity.setId(BigInteger.ONE);
        userEntity.setName("John");
        userEntity.setLastName("Doe");
        userEntity.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userEntity.setAddress("123 Main St");
        userEntity.setEmail("john.doe@example.com");
        userEntity.setPhone("1234567890");
        userEntity.setBaseSalary(new BigDecimal("50000.00"));
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll()).thenReturn(Flux.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getEmail().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getEmail().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    void createUserSuccessfully() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.empty());
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);


        Mono<Void> result = repositoryAdapter.createUser(user);

        StepVerifier.create(result)
                .verifyComplete();
    }

//    @Test
//    void createUserFailsWhenEmailAlreadyExists() {
//        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.just(userEntity));
//
//        Mono<Void> result = repositoryAdapter.createUser(user);
//
//        StepVerifier.create(result)
//                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
//                        throwable.getMessage().equals("Correo ya registrado"))
//                .verify();
//    }

    @Test
    void createUserFailsWithDuplicateKeyException() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.empty());
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new DuplicateKeyException("Duplicate key")));
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);

        Mono<Void> result = repositoryAdapter.createUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().equals("Correo ya registrado"))
                .verify();
    }

    @Test
    void createUserFailsWithGenericException() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Mono.empty());
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("DB error")));
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);

        Mono<Void> result = repositoryAdapter.createUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        throwable.getMessage().equals("No se pudo crear el usuario"))
                .verify();
    }
}

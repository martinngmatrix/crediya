package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.exception.BusinessException;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User/* change for domain model */,
    UserEntity/* change for adapter model */,
    BigInteger,
    MyReactiveRepository
> implements UserRepository {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
    }

    @Transactional
    @Override
    public Mono<Void> createUser(User user) {
        return repository.findByEmail(user.getEmail())
                .flatMap(existingUser -> Mono.<Void>error(new BusinessException("Correo ya registrado")))
                .switchIfEmpty(
                        repository.save(toData(user))
                                .then()
                                .onErrorMap(e -> {
                                    if (e instanceof DuplicateKeyException) {
                                        return new BusinessException("Correo ya registrado");
                                    }
                                    return new BusinessException("No se pudo crear el usuario");
                                })
                );
    }

}

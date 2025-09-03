package co.com.bancolombia.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import co.com.bancolombia.api.dto.CreateUserDTO;
import co.com.bancolombia.api.dto.FindUserDTO;
import co.com.bancolombia.model.user.User;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    List<CreateUserDTO> toResponseList(List<User> users);

    User toModel(CreateUserDTO createUserDTO);
    
    FindUserDTO toFindUserDTO(User user);
    
}

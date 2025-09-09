package co.com.pragma.consumer.mappers;

import co.com.pragma.consumer.UserDto;
import co.com.pragma.model.application.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toModel(UserDto userDto);
}

package de.sxpl.pialgra.mappers;

import de.sxpl.pialgra.domain.dtos.user.CreateUserDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.UserEntity;

public interface UserMapper {
    UserEntity entityFromCreateUserDto(CreateUserDto createUserDto);
    UserEntity entityFromUserDto(UserDto userDto);
    UserDto userDtoFromUserEntity(UserEntity userEntity);
}

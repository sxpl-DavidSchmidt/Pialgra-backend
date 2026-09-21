package de.sxpl.pialgra.mappers;

import de.sxpl.pialgra.domain.dtos.user.CreateUserDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.UserEntity;

public interface UserMapper {
    UserEntity entityFromCreateUserDto(CreateUserDto createUserDto);
    UserDto userDtoFromUserEntity(UserEntity userEntity);
}

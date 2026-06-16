package de.sxpl.pialgra.mappers;

import de.sxpl.pialgra.domain.dtos.user.CreateUserDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.UserEntity;

public interface UserMapper {
    public UserEntity entityFromCreateUserDto(CreateUserDto createUserDto);
    public UserEntity entityFromUserDto(UserDto userDto);
    public UserDto userDtoFromUserEntity(UserEntity userEntity);
}

package de.sxpl.pialgra.mappers.impl;

import de.sxpl.pialgra.domain.dtos.user.CreateUserDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity entityFromCreateUserDto(CreateUserDto createUserDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(createUserDto.getUsername());
        userEntity.setPassword(createUserDto.getPassword());
        return userEntity;
    }

    @Override
    public UserEntity entityFromUserDto(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.getUsername());
        userEntity.setCreatedAt(userDto.getCreatedAt());
        return userEntity;
    }

    @Override
    public UserDto userDtoFromUserEntity(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setUsername(userEntity.getUsername());
        userDto.setCreatedAt(userEntity.getCreatedAt());
        return userDto;
    }
}

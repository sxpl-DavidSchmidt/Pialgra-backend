package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.user.CreateUserDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.UserMapper;
import de.sxpl.pialgra.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(
                userService
                        .getUsers()
                        .stream()
                        .map(userMapper::userDtoFromUserEntity)
                        .toList()
        );
    }

    @PostMapping(path = "/create")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserDto user) {
        UserEntity userEntity = userMapper.entityFromCreateUserDto(user);
        UserEntity savedUserEntity = userService.createUser(userEntity);
        return new ResponseEntity<>(
                userMapper.userDtoFromUserEntity(savedUserEntity),
                HttpStatus.CREATED
        );
    }
}

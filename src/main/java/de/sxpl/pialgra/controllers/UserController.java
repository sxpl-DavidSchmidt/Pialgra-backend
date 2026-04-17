package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.UserDto;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.Mapper;
import de.sxpl.pialgra.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;

    public UserController(UserService userService, Mapper<UserEntity, UserDto> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(path = "/users")
    public Iterable<UserDto> getUsers(
            @RequestParam(required = false) String username
    ) {
        if (username != null) {
            return userService.getUsersByUsername(username)
                    .stream()
                    .map(userMapper::mapTo)
                    .toList();
        }
        return userService.getUsers()
                .stream()
                .map(userMapper::mapTo)
                .toList();
    }

    @PostMapping(path = "/users/create")
    public UserDto createUser(@RequestBody UserDto user) {
        UserEntity userEntity = userMapper.mapFrom(user);
        UserEntity savedUserEntity = userService.createUser(userEntity);
        return userMapper.mapTo(savedUserEntity);
    }
}

package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.UserDto;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.Mapper;
import de.sxpl.pialgra.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {
    private final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;

    public UserController(UserService userService, Mapper<UserEntity, UserDto> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(required = false) String username
    ) {
        List<UserDto> users;
        if (username != null) {
            users = userService.getUsersByUsername(username)
                    .stream()
                    .map(userMapper::mapTo)
                    .toList();
        } else {
            users = userService.getUsers()
                    .stream()
                    .map(userMapper::mapTo)
                    .toList();
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
        UserEntity userEntity = userMapper.mapFrom(user);
        UserEntity savedUserEntity = userService.createUser(userEntity);
        return new ResponseEntity<>(
                userMapper.mapTo(savedUserEntity),
                HttpStatus.CREATED
        );
    }
}

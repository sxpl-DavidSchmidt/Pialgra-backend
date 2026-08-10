package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.mappers.UserMapper;
import de.sxpl.pialgra.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(
                userService
                        .findAll()
                        .stream()
                        .map(userMapper::userDtoFromUserEntity)
                        .toList()
        );
    }
}

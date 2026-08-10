package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.auth.LoginDto;
import de.sxpl.pialgra.domain.dtos.user.CreateUserDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.UserMapper;
import de.sxpl.pialgra.service.AuthService;
import de.sxpl.pialgra.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @PostMapping(path = "/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody CreateUserDto createUserDto) {
        UserEntity userEntity = userMapper.entityFromCreateUserDto(createUserDto);
        UserEntity savedUserEntity = authService.register(userEntity);
        return new ResponseEntity<>(
                userMapper.userDtoFromUserEntity(savedUserEntity),
                HttpStatus.CREATED
        );
    }

    @PostMapping(path = "/login")
    public ResponseEntity<UserDto> login(
            @Valid @RequestBody LoginDto loginDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = authService.authenticate(
                loginDto.getUsername(),
                loginDto.getPassword()
        );

        // Session fixation protection: drop any pre-existing session before the
        // authenticated context is written to a new one.
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return ResponseEntity.ok(currentUser(authentication.getName()));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Removes the row from SPRING_SESSION and expires the session cookie.
            session.invalidate();
        }
        securityContextHolderStrategy.clearContext();
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        return ResponseEntity.ok(currentUser(authentication.getName()));
    }

    private UserDto currentUser(String username) {
        return userService
                .findByUsername(username)
                .map(userMapper::userDtoFromUserEntity)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}

package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.Role;
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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;

import java.util.Set;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final CsrfTokenRepository csrfTokenRepository;

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
    }

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @PostMapping(path = "/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody CreateUserDto createUserDto) {
        UserEntity userEntity = userMapper.entityFromCreateUserDto(createUserDto);
        userEntity.setRoles(Set.of(Role.USER));
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

        new ChangeSessionIdAuthenticationStrategy().onAuthentication(authentication, request, response);
        csrfTokenRepository.saveToken(null, request, response);

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        UserEntity entity = userService
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(userMapper.userDtoFromUserEntity(entity));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate(); // expires the session cookie.

        securityContextHolderStrategy.clearContext();
        return ResponseEntity.noContent().build();
    }
}

package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.exceptions.UsernameAlreadyExistsException;
import de.sxpl.pialgra.service.AuthService;
import de.sxpl.pialgra.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserEntity register(UserEntity user) {
        if (userService.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }
        return userService.createUser(user);
    }

    @Override
    public Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password)
        );
    }
}

package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    UserEntity register(UserEntity user);
    Authentication authenticate(String username, String password);
}

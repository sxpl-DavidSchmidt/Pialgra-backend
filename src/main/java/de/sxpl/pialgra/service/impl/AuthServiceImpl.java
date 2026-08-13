package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.exceptions.UsernameAlreadyExistsException;
import de.sxpl.pialgra.service.AuthService;
import de.sxpl.pialgra.service.CategoryService;
import de.sxpl.pialgra.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CategoryService categoryService;

    @Override
    public UserEntity register(UserEntity user) {
        if (userService.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }
        UserEntity createdUser = userService.createUser(user);

        for (String categoryName: List.of("Algebra", "Geometry", "Psychology", "Programming")) {
            CategoryEntity category = new CategoryEntity();
            category.setName(categoryName);
            category.setUser(createdUser);
            categoryService.createCategory(category, user.getUsername());
        }

        return createdUser;
    }

    @Override
    public Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password)
        );
    }
}

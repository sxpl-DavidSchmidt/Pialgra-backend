package de.sxpl.pialgra.security;

import de.sxpl.pialgra.domain.Role;
import de.sxpl.pialgra.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(userEntity -> User
                        .withUsername(userEntity.getUsername())
                        .password(userEntity.getPassword())
                        .roles(
                                userEntity.getRoles()
                                        .stream()
                                        .map(Role::name)
                                        .toArray(String[]::new)
                        )
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

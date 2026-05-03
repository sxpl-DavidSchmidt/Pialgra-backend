package de.sxpl.pialgra.security;

import de.sxpl.pialgra.domain.entities.UserEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class PialgraUserDetails implements UserDetails {
    private final UserEntity userEntity;

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public @NonNull String getUsername() {
        return userEntity.getUsername();
    }
}

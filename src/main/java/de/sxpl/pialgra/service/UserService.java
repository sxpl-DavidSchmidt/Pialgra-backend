package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.UserEntity;

import java.util.Optional;

public interface UserService {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    UserEntity createUser(UserEntity user);
    void deleteAccount(String username);
}

package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    List<UserEntity> getUsers();
    Optional<UserEntity> getUserByUsername(String username);
    UserEntity createUser(UserEntity user);
}

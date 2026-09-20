package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.ImageEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserEntity> findAll();
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    UserEntity createUser(UserEntity user);
    UserEntity updateProfilePicture(UserEntity user, ImageEntity image);
    ImageEntity getDefaultProfilePicture();
}

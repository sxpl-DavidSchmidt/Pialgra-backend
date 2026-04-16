package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<UserEntity> getUsers();
    UserEntity createUser(UserEntity user);
}

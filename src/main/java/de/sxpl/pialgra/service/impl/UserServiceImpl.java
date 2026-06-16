package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserEntity> getUsers() {
        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .toList();
    }

    @Override
    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        if (userEntity.getCreatedAt() == null) {
            userEntity.setCreatedAt(LocalDate.now());
        }
        return userRepository.save(userEntity);
    }
}

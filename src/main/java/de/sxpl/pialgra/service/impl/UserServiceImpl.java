package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.ImageEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.service.UserService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    public List<UserEntity> findAll() {
        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .toList();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        if (userEntity.getCreatedAt() == null) {
            userEntity.setCreatedAt(LocalDate.now());
        }

        if (userEntity.getProfilePicture() == null) {
            try {
                ClassPathResource resource = new ClassPathResource("static/images/default_profile_picture.png");
                byte[] defaultImageData = resource.getContentAsByteArray();
                ImageEntity defaultImage = new ImageEntity();
                defaultImage.setImageData(defaultImageData);
                userEntity.setProfilePicture(defaultImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load default profile picture", e);
            }
        }
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity updateProfilePicture(UserEntity user, ImageEntity image) {
        user.setProfilePicture(image);
        return userRepository.save(user);
    }
}

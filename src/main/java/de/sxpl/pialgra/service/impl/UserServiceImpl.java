package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.ImageEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.repositories.UserCreationRepository;
import de.sxpl.pialgra.exceptions.UsernameAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
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
    private final UserCreationRepository userCreationRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserCreationRepository userCreationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userCreationRepository = userCreationRepository;
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
    @Transactional
    public UserEntity createUser(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        if (userEntity.getCreatedAt() == null) {
            userEntity.setCreatedAt(LocalDate.now());
        }

        if (userEntity.getProfilePicture() == null) {
            userEntity.setProfilePicture(getDefaultProfilePicture());
        }
        try {
            return userCreationRepository.insert(userEntity);
        } catch (DataIntegrityViolationException e) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }
    }

    @Override
    public UserEntity updateProfilePicture(UserEntity user, ImageEntity image) {
        ImageEntity replacement = image == null ? getDefaultProfilePicture() : image;
        if (user.getProfilePicture() == null) {
            user.setProfilePicture(replacement);
        } else {
            // Reuse this user's image record so replacing a picture leaves no orphan rows.
            user.getProfilePicture().setImageData(replacement.getImageData());
        }
        return userRepository.save(user);
    }

    @Override
    public ImageEntity getDefaultProfilePicture() {
        try {
            ImageEntity image = new ImageEntity();
            image.setImageData(new ClassPathResource("static/images/default_profile_picture.png").getContentAsByteArray());
            return image;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load default profile picture", e);
        }
    }
}

package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.repositories.CategoryRepository;
import de.sxpl.pialgra.repositories.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import de.sxpl.pialgra.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final StudySessionRepository studySessionRepository;
    private final FindByIndexNameSessionRepository<?> sessionRepository;

    @Override
    @Transactional
    public void deleteAccount(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        studySessionRepository.deleteAll(studySessionRepository.findByUser(user));
        categoryRepository.deleteAll(categoryRepository.findByUser(user));
        userRepository.delete(user);
        sessionRepository.findByPrincipalName(username).keySet().forEach(sessionRepository::deleteById);
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

        return userRepository.save(userEntity);
    }
}

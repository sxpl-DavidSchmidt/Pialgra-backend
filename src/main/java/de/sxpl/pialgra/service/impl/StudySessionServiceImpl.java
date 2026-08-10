package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.StudySessionRepository;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudySessionServiceImpl implements StudySessionService {
    private final UserRepository userRepository;
    private final StudySessionRepository studySessionRepository;

    @Override
    public List<StudySessionEntity> findByUsername(
            String username
    ) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        return StreamSupport
                .stream(studySessionRepository.findByUser(userEntity).spliterator(), false)
                .toList();
    }

    @Transactional
    @Override
    public StudySessionEntity createStudySession(
            StudySessionEntity studySession,
            String username
    ) {
        log.info("Creating study session for user {}", username);
        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow();
        studySession.setUser(userEntity);
        return studySessionRepository.save(studySession);
    }
}

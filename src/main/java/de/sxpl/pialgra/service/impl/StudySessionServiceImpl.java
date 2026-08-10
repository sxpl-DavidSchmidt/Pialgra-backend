package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.StudySessionRepository;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class StudySessionServiceImpl implements StudySessionService {
    private final UserRepository userRepository;
    private final StudySessionRepository studySessionRepository;

    @Override
    public List<StudySessionEntity> findAll() {
        return StreamSupport
                .stream(studySessionRepository.findAll().spliterator(), false)
                .toList();
    }

    @Override
    public List<StudySessionEntity> findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        return StreamSupport
                .stream(studySessionRepository.findByUser(userEntity).spliterator(), false)
                .toList();
    }

    @Override
    public Optional<StudySessionEntity> findCurrentByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        return StreamSupport
                .stream(studySessionRepository.findByUser(userEntity).spliterator(), false)
                .filter(entity -> entity.getEndTime() == null)
                .findFirst();
    }

    @Override
    public StudySessionEntity createStudySession(StudySessionEntity studySession) {
        return studySessionRepository.save(studySession);
    }
}

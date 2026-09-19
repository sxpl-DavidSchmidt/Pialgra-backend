package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.StudySessionRepository;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.repositories.CategoryRepository;
import de.sxpl.pialgra.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudySessionServiceImpl implements StudySessionService {
    private final UserRepository userRepository;
    private final StudySessionRepository studySessionRepository;
    private final CategoryRepository categoryRepository;

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
        if (studySession.getCategory() == null || studySession.getCategory().getUser() == null
                || !username.equals(studySession.getCategory().getUser().getUsername())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Category not found");
        }
        if (studySession.getStartTime() == null || studySession.getEndTime() == null
                || !studySession.getEndTime().isAfter(studySession.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        // Serialize new sessions with category deletion and avoid merging a stale category back in.
        studySession.setCategory(categoryRepository.findOwnedForUpdate(studySession.getCategory().getUuid(), username)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Category not found")));
        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow();
        studySession.setUser(userEntity);
        return studySessionRepository.save(studySession);
    }
}

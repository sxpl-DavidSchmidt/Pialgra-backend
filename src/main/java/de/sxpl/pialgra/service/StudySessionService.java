package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface StudySessionService {
    List<StudySessionEntity> findAll();
    List<StudySessionEntity> findByUsername(String username);
    Optional<StudySessionEntity> findCurrentByUsername(String username);
    StudySessionEntity createStudySession(StudySessionEntity studySession);
}

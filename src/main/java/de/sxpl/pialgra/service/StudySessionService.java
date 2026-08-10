package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudySessionService {
    List<StudySessionEntity> findByUsername(String username);
    StudySessionEntity createStudySession(StudySessionEntity studySession, String username);
}

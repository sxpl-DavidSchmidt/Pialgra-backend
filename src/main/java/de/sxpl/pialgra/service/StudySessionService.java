package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudySessionService {
    StudySessionEntity updateStudySession(java.util.UUID uuid, de.sxpl.pialgra.domain.dtos.studysession.UpdateStudySessionDto changes, String username);
    void deleteStudySession(java.util.UUID uuid, String username);
    List<StudySessionEntity> findByUsername(String username);
    StudySessionEntity createStudySession(StudySessionEntity studySession, String username);
}

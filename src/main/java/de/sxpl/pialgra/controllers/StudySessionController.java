package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.studysession.CreateStudySessionDto;
import de.sxpl.pialgra.domain.dtos.studysession.StudySessionDto;
import de.sxpl.pialgra.domain.dtos.studysession.UpdateStudySessionDto;
import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.mappers.StudySessionMapper;
import de.sxpl.pialgra.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/study-sessions")
@RequiredArgsConstructor
public class StudySessionController {
    private final StudySessionService studySessionService;
    private final StudySessionMapper studySessionMapper;

    @PutMapping("/{uuid}")
    public StudySessionDto updateStudySession(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateStudySessionDto changes,
            Authentication authentication
    ) {
        return studySessionMapper.studySessionDtoFromStudySessionEntity(
                studySessionService.updateStudySession(uuid, changes, authentication.getName())
        );
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteStudySession(
            @PathVariable UUID uuid,
            Authentication authentication
    ) {
        studySessionService.deleteStudySession(uuid, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<StudySessionDto> createStudySession(
            @Valid @RequestBody CreateStudySessionDto studySession,
            Authentication authentication
    ) {
        String username = authentication.getName();
        StudySessionEntity entity = studySessionMapper.entityFromCreateStudySessionDto(studySession);
        StudySessionEntity saved = studySessionService.createStudySession(entity, username);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studySessionMapper.studySessionDtoFromStudySessionEntity(saved)
                );
    }
}

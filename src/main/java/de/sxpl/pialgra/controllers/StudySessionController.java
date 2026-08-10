package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.studysession.CreateStudySessionDto;
import de.sxpl.pialgra.domain.dtos.studysession.StudySessionDto;
import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.mappers.StudySessionMapper;
import de.sxpl.pialgra.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/study-sessions")
@RequiredArgsConstructor
public class StudySessionController {
    private final StudySessionService studySessionService;
    private final StudySessionMapper studySessionMapper;

    @GetMapping("/{username}")
    public ResponseEntity<List<StudySessionDto>> getStudySessionsByUsername(@PathVariable String username) {
        List<StudySessionDto> studySessions = studySessionService.findByUsername(username)
                .stream()
                .map(studySessionMapper::studySessionDtoFromStudySessionEntity)
                .toList();
        return ResponseEntity.ok(studySessions);
    }

    @PostMapping
    public ResponseEntity<StudySessionDto> createStudySession(@RequestBody CreateStudySessionDto studySession) {
        StudySessionEntity studySessionEntity = studySessionMapper.entityFromCreateStudySessionDto(studySession);
        StudySessionEntity savedStudySessionEntity = studySessionService.createStudySession(studySessionEntity);
        return new ResponseEntity<>(
                studySessionMapper.studySessionDtoFromStudySessionEntity(savedStudySessionEntity),
                HttpStatus.CREATED
        );
    }
}

package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.session.CreateSessionDto;
import de.sxpl.pialgra.domain.dtos.session.SessionDto;
import de.sxpl.pialgra.domain.entities.SessionEntity;
import de.sxpl.pialgra.mappers.SessionMapper;
import de.sxpl.pialgra.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    @GetMapping("/{username}")
    public ResponseEntity<List<SessionDto>> getSessionsByUsername(@PathVariable String username) {
        List<SessionDto> sessions = sessionService.findByUsername(username)
                .stream()
                .map(sessionMapper::sessionDtoFromSessionEntity)
                .toList();
        return ResponseEntity.ok(sessions);
    }

    @PostMapping
    public ResponseEntity<SessionDto> createSession(@RequestBody CreateSessionDto session) {
        SessionEntity sessionEntity = sessionMapper.entityFromCreateSessionDto(session);
        SessionEntity savedSessionEntity = sessionService.createSession(sessionEntity);
        return new ResponseEntity<>(
                sessionMapper.sessionDtoFromSessionEntity(savedSessionEntity),
                HttpStatus.CREATED
        );
    }
}

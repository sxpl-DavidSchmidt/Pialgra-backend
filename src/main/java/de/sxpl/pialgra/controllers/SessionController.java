package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.SessionDto;
import de.sxpl.pialgra.domain.dtos.UserDto;
import de.sxpl.pialgra.domain.entities.SessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.Mapper;
import de.sxpl.pialgra.service.SessionService;
import de.sxpl.pialgra.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final UserService userService;
    private final SessionService sessionService;
    private final Mapper<SessionEntity, SessionDto> sessionMapper;
    private final Mapper<UserEntity, UserDto> userMapper;

    @GetMapping("/{username}")
    public ResponseEntity<List<SessionDto>> getSessionsByUsername(@PathVariable String username) {
        List<SessionDto> sessions = sessionService.findByUsername(username)
                .stream()
                .map(sessionMapper::mapTo)
                .toList();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{username}/current")
    public ResponseEntity<SessionDto> getCurrentSessionByUsername(@PathVariable String username) {
        SessionEntity session = sessionService.findCurrentByUsername(username).orElseThrow();
        return ResponseEntity.ok(sessionMapper.mapTo(session));
    }

    @PostMapping
    public ResponseEntity<SessionDto> createSession(@RequestBody SessionDto session) {
        SessionEntity sessionEntity = sessionMapper.mapFrom(session);
        SessionEntity savedSessionEntity = sessionService.createSession(sessionEntity);
        return new ResponseEntity<>(
                sessionMapper.mapTo(savedSessionEntity),
                HttpStatus.CREATED
        );
    }
}

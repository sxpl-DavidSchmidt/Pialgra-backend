package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.SessionEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SessionService {
    List<SessionEntity> findAll();
    List<SessionEntity> findByUsername(String username);
    Optional<SessionEntity> findCurrentByUsername(String username);
    SessionEntity createSession(SessionEntity session);
}

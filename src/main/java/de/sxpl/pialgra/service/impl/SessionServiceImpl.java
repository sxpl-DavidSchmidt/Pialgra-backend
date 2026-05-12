package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.SessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.SessionRepository;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Override
    public List<SessionEntity> findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        return StreamSupport
                .stream(sessionRepository.findByUser(userEntity).spliterator(), false)
                .toList();
    }

    @Override
    public Optional<SessionEntity> findCurrentByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        return StreamSupport
                .stream(sessionRepository.findByUser(userEntity).spliterator(), false)
                .filter(entity -> entity.getEndTime() == null)
                .findFirst();
    }

    @Override
    public SessionEntity createSession(SessionEntity session) {
        return sessionRepository.save(session);
    }
}

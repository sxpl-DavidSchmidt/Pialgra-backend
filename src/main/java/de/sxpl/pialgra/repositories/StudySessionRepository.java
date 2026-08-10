package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface StudySessionRepository extends CrudRepository<StudySessionEntity, UUID> {
    Iterable<StudySessionEntity> findByUser(UserEntity user);
}

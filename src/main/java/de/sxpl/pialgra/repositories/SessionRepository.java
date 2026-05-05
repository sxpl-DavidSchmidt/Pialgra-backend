package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.SessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface SessionRepository extends CrudRepository<SessionEntity, UUID> {
    Iterable<SessionEntity> findByUser(UserEntity user);
    Iterable<SessionEntity> findByUserAndStartTimeAfter(UserEntity user, LocalDateTime startTimeAfter);
    Iterable<SessionEntity> findByUserAndCategoryAndStartTimeAfter(UserEntity user, CategoryEntity category, LocalDateTime startTimeAfter);
}

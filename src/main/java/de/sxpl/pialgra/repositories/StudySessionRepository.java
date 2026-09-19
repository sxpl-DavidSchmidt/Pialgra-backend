package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface StudySessionRepository extends CrudRepository<StudySessionEntity, UUID> {
    Iterable<StudySessionEntity> findByUser(UserEntity user);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update StudySessionEntity s set s.category = null where s.category.uuid = :categoryUuid")
    int clearCategory(@Param("categoryUuid") UUID categoryUuid);
}

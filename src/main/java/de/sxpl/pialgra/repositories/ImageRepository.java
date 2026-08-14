package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.domain.entities.ImageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository

public interface ImageRepository extends CrudRepository<ImageEntity, UUID> {
    Optional<ImageEntity> findByUuid(UUID uuid);
}

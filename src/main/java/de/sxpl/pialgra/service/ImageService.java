package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.ImageEntity;

import java.util.Optional;
import java.util.UUID;

public interface ImageService {
    Optional<ImageEntity> findByUuid(UUID uuid);
    ImageEntity createImage(ImageEntity image);
}

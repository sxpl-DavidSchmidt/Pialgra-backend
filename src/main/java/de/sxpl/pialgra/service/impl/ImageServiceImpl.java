package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.ImageEntity;
import de.sxpl.pialgra.repositories.ImageRepository;
import de.sxpl.pialgra.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    public Optional<ImageEntity> findByUuid(UUID uuid) {
        return imageRepository.findByUuid(uuid);
    }

    @Override
    public ImageEntity createImage(ImageEntity image) {
        return imageRepository.save(image);
    }
}

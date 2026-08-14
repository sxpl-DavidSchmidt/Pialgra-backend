package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.image.ImageDto;
import de.sxpl.pialgra.domain.dtos.studysession.StudySessionDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.ImageEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.mappers.StudySessionMapper;
import de.sxpl.pialgra.mappers.UserMapper;
import de.sxpl.pialgra.service.CategoryService;
import de.sxpl.pialgra.service.ImageService;
import de.sxpl.pialgra.service.StudySessionService;
import de.sxpl.pialgra.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final StudySessionService studySessionService;
    private final StudySessionMapper studySessionMapper;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final ImageService imageService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(
                userService
                        .findAll()
                        .stream()
                        .map(userMapper::userDtoFromUserEntity)
                        .toList()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(
                userMapper.userDtoFromUserEntity(
                        userService
                                .findByUsername(username)
                                .orElseThrow()
                )
        );
    }

    @GetMapping("/me/study-sessions")
    public ResponseEntity<List<StudySessionDto>> getStudySessionsByCurrentUser(
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(
                studySessionService
                        .findByUsername(username)
                        .stream()
                        .map(studySessionMapper::studySessionDtoFromStudySessionEntity)
                        .toList()
        );
    }

    @GetMapping("/me/categories")
    public ResponseEntity<List<CategoryDto>> getCategoriesByCurrentUser(
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(
                categoryService
                        .findByUsername(username)
                        .stream()
                        .map(categoryMapper::categoryDtoFromCategoryEntity)
                        .toList()
        );
    }

    @PutMapping("/me/profile-picture")
    public ResponseEntity<Map<String, Object>> uploadProfilePicture(
            Authentication authentication,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Empty file"));
        }

        // TODO: Move logic to service
        int width, height;
        try (ImageInputStream stream = ImageIO.createImageInputStream(imageFile.getInputStream())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) return ResponseEntity.badRequest().body(Map.of("error", "Unsupported file format"));

            ImageReader reader = readers.next();
            reader.setInput(stream);
            width = reader.getWidth(reader.getMinIndex());
            height = reader.getHeight(reader.getMinIndex());
            reader.dispose();
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error reading image"));
        }

        if (width > 512 || height > 512 || width != height) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image must be square and no larger than 512x512"));
        }

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImageData(imageFile.getBytes());
        ImageEntity savedImageEntity = imageService.createImage(imageEntity);

        String username = authentication.getName();
        UserEntity userEntity = userService.findByUsername(username).orElseThrow();
        userService.updateProfilePicture(userEntity, savedImageEntity);

        return ResponseEntity.ok(Map.of("message", "Profile picture updated"));
    }

    @GetMapping("/me/profile-picture")
    public ResponseEntity<ImageDto> getProfilePictureByCurrentUser(
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserEntity userEntity = userService.findByUsername(username).orElseThrow();

        ImageEntity imageEntity = imageService.findByUuid(userEntity.getProfilePicture().getUuid()).orElseThrow();

        // TODO: Implement mapper
        ImageDto imageDto = new ImageDto();
        imageDto.setUuid(imageEntity.getUuid());
        imageDto.setImageData(imageEntity.getImageData());

        return ResponseEntity.ok(imageDto);
    }
}

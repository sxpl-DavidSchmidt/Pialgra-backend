package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.image.ImageDto;
import de.sxpl.pialgra.domain.dtos.studysession.StudySessionDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.ImageEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.mappers.StudySessionMapper;
import de.sxpl.pialgra.mappers.UserMapper;
import de.sxpl.pialgra.service.CategoryService;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
    public ResponseEntity<?> uploadProfilePicture(
            Authentication authentication,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Empty file"));
        }
        if (imageFile.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.status(413).body(Map.of("error", "Image must be no larger than 10 MB"));
        }

        // TODO: Move logic to service
        BufferedImage decoded;
        try (var input = imageFile.getInputStream(); ImageInputStream stream = ImageIO.createImageInputStream(input)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) return ResponseEntity.badRequest().body(Map.of("error", "Unsupported file format"));

            ImageReader reader = readers.next();
            try {
                String format = reader.getFormatName();
                if (!format.equalsIgnoreCase("PNG") && !format.equalsIgnoreCase("JPEG")) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Choose a PNG or JPG image"));
                }
                reader.setInput(stream);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width != height || width > 512 || width < 1) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Image must be square and no larger than 512 × 512 pixels"));
                }
                decoded = reader.read(0);
            } finally {
                reader.dispose();
            }
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error reading image"));
        }

        // Store a consistent format for the profile and navbar image data URLs.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(decoded, "png", output);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImageData(output.toByteArray());

        String username = authentication.getName();
        UserEntity userEntity = userService.findByUsername(username).orElseThrow();
        UserEntity updatedUser = userService.updateProfilePicture(userEntity, imageEntity);

        return ResponseEntity.ok(toImageDto(updatedUser.getProfilePicture()));
    }

    @DeleteMapping("/me/profile-picture")
    public ResponseEntity<ImageDto> removeProfilePicture(Authentication authentication) {
        UserEntity user = userService.findByUsername(authentication.getName()).orElseThrow();
        UserEntity updatedUser = userService.updateProfilePicture(user, null);
        return ResponseEntity.ok(toImageDto(updatedUser.getProfilePicture()));
    }

    @GetMapping("/me/profile-picture")
    public ResponseEntity<ImageDto> getProfilePictureByCurrentUser(
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserEntity userEntity = userService.findByUsername(username).orElseThrow();

        ImageEntity image = userEntity.getProfilePicture();
        return ResponseEntity.ok(toImageDto(image == null ? userService.getDefaultProfilePicture() : image));
    }

    private ImageDto toImageDto(ImageEntity image) {
        return new ImageDto(image.getUuid(), image.getImageData());
    }
}

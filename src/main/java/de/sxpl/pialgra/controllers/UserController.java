package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.studysession.StudySessionDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
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

import java.util.List;

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
}

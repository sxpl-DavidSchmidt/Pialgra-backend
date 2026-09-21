package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.category.CreateCategoryDto;
import de.sxpl.pialgra.domain.dtos.category.UpdateCategoryDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.service.CategoryService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PutMapping("/{uuid}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateCategoryDto categoryDto,
            Authentication authentication
    ) {
        CategoryEntity updated = categoryService.updateCategory(
                uuid, categoryDto.getName(), categoryDto.getColor(), authentication.getName());
        return ResponseEntity.ok(categoryMapper.categoryDtoFromCategoryEntity(updated));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID uuid, Authentication authentication) {
        categoryService.deleteCategory(uuid, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CreateCategoryDto categoryDto,
            Authentication authentication
    ) {
        String username = authentication.getName();

        CategoryEntity entity =
                categoryMapper.entityFromCreateCategoryDto(categoryDto);

        CategoryEntity savedCategoryEntity =
                categoryService.createCategory(entity, username);

        return new ResponseEntity<>(
                categoryMapper.categoryDtoFromCategoryEntity(savedCategoryEntity),
                HttpStatus.CREATED
        );
    }
}

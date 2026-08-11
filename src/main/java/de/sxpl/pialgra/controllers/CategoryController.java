package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.category.CreateCategoryDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @RequestBody CreateCategoryDto categoryDto,
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

package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.category.CreateCategoryDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper CategoryMapper;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<CategoryDto> categories = categoryService.findAll()
                .stream()
                .map(CategoryMapper::categoryDtoFromCategoryEntity)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByUsername(@PathVariable String username) {
        List<CategoryDto> categories = categoryService.findByUsername(username)
                .stream()
                .map(CategoryMapper::categoryDtoFromCategoryEntity)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CreateCategoryDto categoryDto) {
        CategoryEntity categoryEntity = CategoryMapper.entityFromCreateCategoryDto(categoryDto);
        CategoryEntity savedCategoryEntity = categoryService.createCategory(categoryEntity);
        return new ResponseEntity<>(
                categoryMapper.categoryDtoFromCategoryEntity(savedCategoryEntity),
                HttpStatus.CREATED
        );
    }
}

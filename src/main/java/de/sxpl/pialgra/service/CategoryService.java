package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.CategoryEntity;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryEntity> findByUsername(String username);
    CategoryEntity createCategory(CategoryEntity category, String username);
    CategoryEntity updateCategory(UUID categoryUuid, String name, String color, String username);
    void deleteCategory(UUID categoryUuid, String username);
}

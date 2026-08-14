package de.sxpl.pialgra.service;

import de.sxpl.pialgra.domain.entities.CategoryEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryEntity> findByUsername(String username);
    CategoryEntity createCategory(CategoryEntity category, String username);
}

package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.CategoryRepository;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import de.sxpl.pialgra.repositories.StudySessionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StudySessionRepository studySessionRepository;

    @Override
    @Transactional
    public CategoryEntity updateCategory(UUID categoryUuid, String name, String color, String username) {
        CategoryEntity category = categoryRepository.findOwnedForUpdate(categoryUuid, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        category.setName(name.trim());
        category.setColor(color);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryUuid, String username) {
        CategoryEntity category = categoryRepository.findOwnedForUpdate(categoryUuid, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        studySessionRepository.clearCategory(categoryUuid);
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryEntity> findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        return StreamSupport
                .stream(categoryRepository.findByUser(userEntity).spliterator(), false)
                .toList();
    }

    @Override
    public CategoryEntity createCategory(
            CategoryEntity category,
            String username
    ) {
        if (category.getName() == null || category.getName().isBlank() || category.getName().length() > 100) {
            throw new IllegalArgumentException("Category name must contain between 1 and 100 characters.");
        }
        category.setName(category.getName().trim());
        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow();
        category.setUser(userEntity);
        return categoryRepository.save(category);
    }
}

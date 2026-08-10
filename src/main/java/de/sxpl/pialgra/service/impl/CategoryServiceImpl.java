package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.CategoryRepository;
import de.sxpl.pialgra.repositories.UserRepository;
import de.sxpl.pialgra.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<CategoryEntity> findAll() {
        return StreamSupport
                .stream(categoryRepository.findAll().spliterator(), false)
                .toList();
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
        UserEntity userEntity = userRepository
                .findByUsername(username)
                .orElseThrow();
        category.setUser(userEntity);
        return categoryRepository.save(category);
    }
}

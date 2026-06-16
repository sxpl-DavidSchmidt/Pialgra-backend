package de.sxpl.pialgra.mappers.impl;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapperImpl implements CategoryMapper {
    private final UserMapper userMapper;

    @Override
    public CategoryDto categoryDtoFromCategoryEntity(CategoryEntity categoryEntity) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(categoryEntity.getName());
        categoryDto.setUuid(categoryEntity.getUuid());
        categoryDto.setUser(userMapper.userDtoFromUserEntity(categoryEntity.getUser()));
        return categoryDto;
    }
}

package de.sxpl.pialgra.mappers;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.category.CreateCategoryDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;

public interface CategoryMapper {
    CategoryEntity entityFromCreateCategoryDto(CreateCategoryDto createCategoryDto);
    CategoryDto categoryDtoFromCategoryEntity(CategoryEntity categoryEntity);
}

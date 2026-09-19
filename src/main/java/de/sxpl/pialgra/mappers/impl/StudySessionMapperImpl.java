package de.sxpl.pialgra.mappers.impl;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.studysession.CreateStudySessionDto;
import de.sxpl.pialgra.domain.dtos.studysession.StudySessionDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.mappers.StudySessionMapper;
import de.sxpl.pialgra.mappers.UserMapper;
import de.sxpl.pialgra.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class StudySessionMapperImpl implements StudySessionMapper {
    private final UserMapper userMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public StudySessionEntity entityFromCreateStudySessionDto(CreateStudySessionDto createStudySessionDto) {
        StudySessionEntity studySessionEntity = new StudySessionEntity();

        CategoryEntity categoryEntity = categoryRepository
                .findById(createStudySessionDto.getCategoryUuid())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Category not found"));
        studySessionEntity.setCategory(categoryEntity);

        studySessionEntity.setStartTime(createStudySessionDto.getStartTime().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
        studySessionEntity.setEndTime(createStudySessionDto.getEndTime().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
        return studySessionEntity;
    }

    @Override
    public StudySessionDto studySessionDtoFromStudySessionEntity(StudySessionEntity studySessionEntity) {
        StudySessionDto studySessionDto = new StudySessionDto();

        CategoryDto categoryDto = studySessionEntity.getCategory() == null ? null
                : categoryMapper.categoryDtoFromCategoryEntity(studySessionEntity.getCategory());
        studySessionDto.setCategory(categoryDto);

        UserDto userDto = userMapper.userDtoFromUserEntity(studySessionEntity.getUser());
        studySessionDto.setUser(userDto);

        studySessionDto.setUuid(studySessionEntity.getUuid());
        studySessionDto.setStartTime(studySessionEntity.getStartTime().atOffset(ZoneOffset.UTC));
        studySessionDto.setEndTime(studySessionEntity.getEndTime() == null ? null : studySessionEntity.getEndTime().atOffset(ZoneOffset.UTC));
        return studySessionDto;
    }
}

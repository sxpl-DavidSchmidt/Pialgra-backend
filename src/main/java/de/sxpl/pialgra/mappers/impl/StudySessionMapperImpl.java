package de.sxpl.pialgra.mappers.impl;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.studysession.CreateStudySessionDto;
import de.sxpl.pialgra.domain.dtos.studysession.StudySessionDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.mappers.StudySessionMapper;
import de.sxpl.pialgra.mappers.UserMapper;
import de.sxpl.pialgra.repositories.CategoryRepository;
import de.sxpl.pialgra.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudySessionMapperImpl implements StudySessionMapper {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public StudySessionEntity entityFromCreateStudySessionDto(CreateStudySessionDto createStudySessionDto) {
        StudySessionEntity studySessionEntity = new StudySessionEntity();

        UserEntity userEntity = userRepository
                .findByUsername(createStudySessionDto.getUsername())
                .orElseThrow();
        studySessionEntity.setUser(userEntity);

        CategoryEntity categoryEntity = categoryRepository
                .findById(createStudySessionDto.getCategoryUuid())
                .orElseThrow();
        studySessionEntity.setCategory(categoryEntity);

        studySessionEntity.setStartTime(createStudySessionDto.getStartTime());
        studySessionEntity.setEndTime(createStudySessionDto.getEndTime());
        return studySessionEntity;
    }

    @Override
    public StudySessionDto studySessionDtoFromStudySessionEntity(StudySessionEntity studySessionEntity) {
        StudySessionDto studySessionDto = new StudySessionDto();

        CategoryDto categoryDto = categoryMapper.categoryDtoFromCategoryEntity(studySessionEntity.getCategory());
        studySessionDto.setCategory(categoryDto);

        UserDto userDto = userMapper.userDtoFromUserEntity(studySessionEntity.getUser());
        studySessionDto.setUser(userDto);

        studySessionDto.setUuid(studySessionEntity.getUuid());
        studySessionDto.setStartTime(studySessionEntity.getStartTime());
        studySessionDto.setEndTime(studySessionEntity.getEndTime());
        return studySessionDto;
    }
}

package de.sxpl.pialgra.mappers.impl;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.session.CreateSessionDto;
import de.sxpl.pialgra.domain.dtos.session.SessionDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.SessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.mappers.CategoryMapper;
import de.sxpl.pialgra.mappers.SessionMapper;
import de.sxpl.pialgra.mappers.UserMapper;
import de.sxpl.pialgra.repositories.CategoryRepository;
import de.sxpl.pialgra.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionMapperImp implements SessionMapper {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public SessionEntity entityFromCreateSessionDto(CreateSessionDto createSessionDto) {
        SessionEntity sessionEntity = new SessionEntity();

        UserEntity userEntity = userRepository
                .findByUsername(createSessionDto.getUsername())
                .orElseThrow();
        sessionEntity.setUser(userEntity);

        CategoryEntity categoryEntity = categoryRepository
                .findById(createSessionDto.getCategoryUuid())
                .orElseThrow();
        sessionEntity.setCategory(categoryEntity);

        sessionEntity.setStartTime(createSessionDto.getStartTime());
        sessionEntity.setEndTime(createSessionDto.getEndTime());
        return sessionEntity;
    }

    @Override
    public SessionDto sessionDtoFromSessionEntity(SessionEntity sessionEntity) {
        SessionDto sessionDto = new SessionDto();

        CategoryDto categoryDto = categoryMapper.categoryDtoFromCategoryEntity(sessionEntity.getCategory());
        sessionDto.setCategory(categoryDto);

        UserDto userDto = userMapper.userDtoFromUserEntity(sessionEntity.getUser());
        sessionDto.setUser(userDto);

        sessionDto.setUuid(sessionEntity.getUuid());
        sessionDto.setStartTime(sessionEntity.getStartTime());
        sessionDto.setEndTime(sessionEntity.getEndTime());
        return sessionDto;
    }
}

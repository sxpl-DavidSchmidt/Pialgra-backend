package de.sxpl.pialgra.mappers.impl;

import de.sxpl.pialgra.domain.dtos.SessionDto;
import de.sxpl.pialgra.domain.entities.SessionEntity;
import de.sxpl.pialgra.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionMapperImp implements Mapper<SessionEntity, SessionDto> {
    private final ModelMapper modelMapper;

    @Override
    public SessionDto mapTo(SessionEntity sessionEntity) {
        return modelMapper.map(sessionEntity, SessionDto.class);
    }

    @Override
    public SessionEntity mapFrom(SessionDto sessionDto) {
        return modelMapper.map(sessionDto, SessionEntity.class);
    }
}

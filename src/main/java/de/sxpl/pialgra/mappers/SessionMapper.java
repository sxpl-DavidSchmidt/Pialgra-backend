package de.sxpl.pialgra.mappers;

import de.sxpl.pialgra.domain.dtos.session.CreateSessionDto;
import de.sxpl.pialgra.domain.dtos.session.SessionDto;
import de.sxpl.pialgra.domain.entities.SessionEntity;

public interface SessionMapper {
    SessionEntity entityFromCreateSessionDto(CreateSessionDto createSessionDto);
    SessionDto sessionDtoFromSessionEntity(SessionEntity sessionEntity);
}

package de.sxpl.pialgra.mappers;

import de.sxpl.pialgra.domain.dtos.studysession.CreateStudySessionDto;
import de.sxpl.pialgra.domain.dtos.studysession.StudySessionDto;
import de.sxpl.pialgra.domain.entities.StudySessionEntity;

public interface StudySessionMapper {
    StudySessionEntity entityFromCreateStudySessionDto(CreateStudySessionDto createStudySessionDto);
    StudySessionDto studySessionDtoFromStudySessionEntity(StudySessionEntity studySessionEntity);
}

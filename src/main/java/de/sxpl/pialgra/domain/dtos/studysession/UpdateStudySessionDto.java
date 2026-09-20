package de.sxpl.pialgra.domain.dtos.studysession;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UpdateStudySessionDto(UUID categoryUuid, @NotNull OffsetDateTime startTime,
                                    @NotNull OffsetDateTime endTime) {}

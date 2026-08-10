package de.sxpl.pialgra.domain.dtos.studysession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudySessionDto {
    private UUID categoryUuid;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

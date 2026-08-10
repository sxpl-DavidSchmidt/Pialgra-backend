package de.sxpl.pialgra.domain.dtos.studysession;

import de.sxpl.pialgra.domain.dtos.category.CategoryDto;
import de.sxpl.pialgra.domain.dtos.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudySessionDto {
    private UUID uuid;
    private UserDto user;
    private CategoryDto category;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

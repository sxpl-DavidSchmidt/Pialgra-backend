package de.sxpl.pialgra.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {
    private UUID uuid;
    private CategoryDto category;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

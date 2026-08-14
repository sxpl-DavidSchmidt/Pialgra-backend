package de.sxpl.pialgra.domain.dtos.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    private UUID uuid;
    private byte[] imageData;
}

package de.sxpl.pialgra.domain.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryDto {
    @NotBlank
    @Size(max = 100)
    private String name;
}

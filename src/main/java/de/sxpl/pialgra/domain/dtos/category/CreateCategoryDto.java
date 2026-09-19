package de.sxpl.pialgra.domain.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryDto {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Color must be a valid HEX color")
    private String color;
}

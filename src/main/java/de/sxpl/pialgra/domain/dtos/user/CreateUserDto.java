package de.sxpl.pialgra.domain.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    @NotBlank(message = "Username must not be blank.")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters.")
    private String username;

    @NotBlank(message = "Password must not be blank.")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
    private String password;
}

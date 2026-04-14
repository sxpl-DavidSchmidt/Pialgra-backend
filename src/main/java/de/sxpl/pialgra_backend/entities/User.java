package de.sxpl.pialgra_backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {
    @NotNull(message = "id cannot be null")
    private String username;
}

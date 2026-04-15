package de.sxpl.pialgra.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class CategoryEntity {
    @Id
    private UUID caregory_id;

    @ManyToOne(cascade = CascadeType.ALL)
    private UserEntity user;

    private String name;
}

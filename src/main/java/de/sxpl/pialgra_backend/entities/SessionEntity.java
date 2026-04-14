package de.sxpl.pialgra_backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="time_entry")
public class SessionEntity {
    @Id
    private UUID uuid;
    private UUID user_id;
    private UUID category_id;
    private Date start;
    private Date end;
}

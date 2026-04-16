package de.sxpl.pialgra;

import de.sxpl.pialgra.entities.CategoryEntity;
import de.sxpl.pialgra.entities.SessionEntity;
import de.sxpl.pialgra.entities.UserEntity;

import java.time.LocalDateTime;

public class TestDataUtility {
    public static UserEntity generateUser() {
        return new UserEntity(null, "username");
    }

    public static CategoryEntity generateCategory() {
        return new CategoryEntity(
                null,
                null,
                "category"
        );
    }

    public static SessionEntity generateSession() {
        return new SessionEntity(
                null,
                null,
                null,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 1, 30, 0)
        );
    }
}

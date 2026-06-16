package de.sxpl.pialgra;

import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.SessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;

import java.time.LocalDateTime;

public class TestDataUtility {
    private static int userCounter = 0;

    public static UserEntity generateUser() {
        UserEntity entity = new UserEntity();
        entity.setUsername("username" + userCounter++);
        entity.setPassword("password");
        return entity;
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

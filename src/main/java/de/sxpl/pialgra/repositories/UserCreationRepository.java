package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.domain.entities.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCreationRepository {
    private final EntityManager entityManager;

    public UserEntity insert(UserEntity user) {
        // Assigned usernames must be inserted, never merged into an existing account.
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }
}

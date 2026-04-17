package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Iterable<UserEntity> findAllByUsername(String username);
}


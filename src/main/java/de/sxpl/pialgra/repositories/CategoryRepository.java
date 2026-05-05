package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends CrudRepository<CategoryEntity, UUID> {
    Iterable<CategoryEntity> findByUser(UserEntity user);
}

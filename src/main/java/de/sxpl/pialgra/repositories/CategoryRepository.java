package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CategoryRepository extends CrudRepository<CategoryEntity, UUID> {
    Iterable<CategoryEntity> findByUser(UserEntity user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CategoryEntity c where c.uuid = :uuid and c.user.username = :username")
    Optional<CategoryEntity> findOwnedForUpdate(@Param("uuid") UUID uuid, @Param("username") String username);
}

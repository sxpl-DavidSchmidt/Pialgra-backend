package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.TestDataUtility;
import de.sxpl.pialgra.entities.CategoryEntity;
import de.sxpl.pialgra.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CategoryRepositoryIntegrationTests {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryRepositoryIntegrationTests(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Test
    public void testCreatingCategory() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        categoryEntity.setUser(userEntity);
        categoryRepository.save(categoryEntity);

        Optional<CategoryEntity> result = categoryRepository.findById(categoryEntity.getUuid());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(categoryEntity);
    }

    @Test
    public void testReadingCategory() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        categoryEntity.setUser(userEntity);
        categoryRepository.save(categoryEntity);

        Optional<CategoryEntity> result = categoryRepository.findById(categoryEntity.getUuid());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("category");
    }

    @Test
    public void testUpdatingCategory() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        categoryEntity.setUser(userEntity);
        categoryRepository.save(categoryEntity);

        categoryEntity.setName("updatedCategory");
        categoryRepository.save(categoryEntity);

        Optional<CategoryEntity> result = categoryRepository.findById(categoryEntity.getUuid());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(categoryEntity);
    }

    @Test
    public void testDeletingCategory() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        categoryEntity.setUser(userEntity);
        categoryRepository.save(categoryEntity);

        categoryRepository.deleteById(categoryEntity.getUuid());

        Optional<CategoryEntity> result = categoryRepository.findById(categoryEntity.getUuid());
        assertThat(result).isEmpty();
    }
}

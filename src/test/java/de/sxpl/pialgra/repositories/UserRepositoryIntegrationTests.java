package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.TestDataUtility;
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
public class UserRepositoryIntegrationTests {
    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryIntegrationTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void testCreatingUser() {
        UserEntity userEntity = TestDataUtility.generateUser();
        userRepository.save(userEntity);

        Optional<UserEntity> result = userRepository.findById(userEntity.getUuid());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userEntity);
    }

    @Test
    public void testReadingUser() {
        UserEntity userEntity = TestDataUtility.generateUser();
        userRepository.save(userEntity);

        Optional<UserEntity> result = userRepository.findById(userEntity.getUuid());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userEntity);
    }

    @Test
    public void testUpdatingUser() {
        UserEntity userEntity = TestDataUtility.generateUser();
        userRepository.save(userEntity);

        userEntity.setUsername("updatedName");
        userRepository.save(userEntity);

        Optional<UserEntity> result = userRepository.findById(userEntity.getUuid());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userEntity);
    }

    @Test
    public void testDeletingUser() {
        UserEntity userEntity = TestDataUtility.generateUser();
        userRepository.save(userEntity);

        userRepository.deleteById(userEntity.getUuid());

        Optional<UserEntity> result = userRepository.findById(userEntity.getUuid());
        assertThat(result).isEmpty();
    }
}

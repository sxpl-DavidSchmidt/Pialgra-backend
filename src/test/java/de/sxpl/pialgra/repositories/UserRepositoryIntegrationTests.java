package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.TestDataUtility;
import de.sxpl.pialgra.domain.entities.UserEntity;
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

        Optional<UserEntity> result = userRepository.findByUsername(userEntity.getUsername());
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(userEntity.getUsername());
        assertThat(result.get().getPassword()).isEqualTo(userEntity.getPassword());
        assertThat(result.get().getCreatedAt()).isNotNull();
    }

    @Test
    public void testReadingUser() {
        UserEntity userEntity = TestDataUtility.generateUser();
        userRepository.save(userEntity);

        Optional<UserEntity> result = userRepository.findByUsername(userEntity.getUsername());
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(userEntity.getUsername());
        assertThat(result.get().getPassword()).isEqualTo(userEntity.getPassword());
        assertThat(result.get().getCreatedAt()).isNotNull();
    }

    @Test
    public void testUpdatingUser() {
        UserEntity userEntity = TestDataUtility.generateUser();
        UserEntity savedUser = userRepository.save(userEntity);

        savedUser.setPassword("updatedPassword");
        userRepository.save(savedUser);

        Optional<UserEntity> result = userRepository.findByUsername(savedUser.getUsername());
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(savedUser.getUsername());
        assertThat(result.get().getPassword()).isEqualTo("updatedPassword");
        assertThat(result.get().getCreatedAt()).isNotNull();
    }

    @Test
    public void testDeletingUser() {
        UserEntity userEntity = TestDataUtility.generateUser();
        userRepository.save(userEntity);

        userRepository.delete(userEntity);

        Optional<UserEntity> result = userRepository.findByUsername(userEntity.getUsername());
        assertThat(result).isEmpty();
    }
}

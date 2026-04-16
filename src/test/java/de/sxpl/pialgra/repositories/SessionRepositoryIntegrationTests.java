package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.TestDataUtility;
import de.sxpl.pialgra.entities.CategoryEntity;
import de.sxpl.pialgra.entities.SessionEntity;
import de.sxpl.pialgra.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SessionRepositoryIntegrationTests {
    private final SessionRepository sessionRepository;

    @Autowired
    public SessionRepositoryIntegrationTests(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Test
    public void testCreatingSession() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        SessionEntity sessionEntity = TestDataUtility.generateSession();
        sessionEntity.setUser(userEntity);
        sessionEntity.setCategory(categoryEntity);

        sessionRepository.save(sessionEntity);

        Optional<SessionEntity> result = sessionRepository.findById(sessionEntity.getUuid());
        assertThat(result).contains(sessionEntity);
    }

    @Test
    public void testReadingSession() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        SessionEntity sessionEntity = TestDataUtility.generateSession();
        sessionEntity.setUser(userEntity);
        sessionEntity.setCategory(categoryEntity);

        sessionRepository.save(sessionEntity);

        Optional<SessionEntity> result = sessionRepository.findById(sessionEntity.getUuid());
        assertThat(result).contains(sessionEntity);
    }

    @Test
    public void testUpdatingSession() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        SessionEntity sessionEntity = TestDataUtility.generateSession();
        sessionEntity.setUser(userEntity);
        sessionEntity.setCategory(categoryEntity);

        sessionRepository.save(sessionEntity);

        sessionEntity.setEndTime(LocalDateTime.of(2020, 1, 1, 2, 0, 0));
        sessionRepository.save(sessionEntity);

        Optional<SessionEntity> result = sessionRepository.findById(sessionEntity.getUuid());
        assertThat(result).contains(sessionEntity);
    }

    @Test
    public void testDeletingSession() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        SessionEntity sessionEntity = TestDataUtility.generateSession();
        sessionEntity.setUser(userEntity);
        sessionEntity.setCategory(categoryEntity);

        sessionRepository.save(sessionEntity);
        sessionRepository.deleteById(sessionEntity.getUuid());

        Optional<SessionEntity> result = sessionRepository.findById(sessionEntity.getUuid());
        assertThat(result).isEmpty();
    }
}

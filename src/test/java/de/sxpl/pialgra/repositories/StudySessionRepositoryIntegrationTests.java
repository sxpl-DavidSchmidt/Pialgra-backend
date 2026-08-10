package de.sxpl.pialgra.repositories;

import de.sxpl.pialgra.TestDataUtility;
import de.sxpl.pialgra.domain.entities.CategoryEntity;
import de.sxpl.pialgra.domain.entities.StudySessionEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
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
public class StudySessionRepositoryIntegrationTests {
    private final StudySessionRepository studySessionRepository;

    @Autowired
    public StudySessionRepositoryIntegrationTests(StudySessionRepository studySessionRepository) {
        this.studySessionRepository = studySessionRepository;
    }

    @Test
    public void testCreatingStudySession() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        StudySessionEntity studySessionEntity = TestDataUtility.generateStudySession();
        studySessionEntity.setUser(userEntity);
        studySessionEntity.setCategory(categoryEntity);

        studySessionRepository.save(studySessionEntity);

        Optional<StudySessionEntity> result = studySessionRepository.findById(studySessionEntity.getUuid());
        assertThat(result).contains(studySessionEntity);
    }

    @Test
    public void testReadingStudySession() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        StudySessionEntity studySessionEntity = TestDataUtility.generateStudySession();
        studySessionEntity.setUser(userEntity);
        studySessionEntity.setCategory(categoryEntity);

        studySessionRepository.save(studySessionEntity);

        Optional<StudySessionEntity> result = studySessionRepository.findById(studySessionEntity.getUuid());
        assertThat(result).contains(studySessionEntity);
    }

    @Test
    public void testUpdatingStudySession() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        StudySessionEntity studySessionEntity = TestDataUtility.generateStudySession();
        studySessionEntity.setUser(userEntity);
        studySessionEntity.setCategory(categoryEntity);

        studySessionRepository.save(studySessionEntity);

        studySessionEntity.setEndTime(LocalDateTime.of(2020, 1, 1, 2, 0, 0));
        studySessionRepository.save(studySessionEntity);

        Optional<StudySessionEntity> result = studySessionRepository.findById(studySessionEntity.getUuid());
        assertThat(result).contains(studySessionEntity);
    }

    @Test
    public void testDeletingStudySession() {
        UserEntity userEntity = TestDataUtility.generateUser();
        CategoryEntity categoryEntity = TestDataUtility.generateCategory();
        StudySessionEntity studySessionEntity = TestDataUtility.generateStudySession();
        studySessionEntity.setUser(userEntity);
        studySessionEntity.setCategory(categoryEntity);

        studySessionRepository.save(studySessionEntity);
        studySessionRepository.deleteById(studySessionEntity.getUuid());

        Optional<StudySessionEntity> result = studySessionRepository.findById(studySessionEntity.getUuid());
        assertThat(result).isEmpty();
    }
}

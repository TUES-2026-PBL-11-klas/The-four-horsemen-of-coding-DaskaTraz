package com.example.ElDnevniko.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.ElDnevniko.domain.entities.SchoolClassEntity;
import com.example.ElDnevniko.repositories.SchoolClassRepository;
import com.example.ElDnevniko.testUtils.TestEntityData;


@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SchoolClassRepositoryTest 
{

    @Autowired private SchoolClassRepository schoolClassRepository;

    @Test
    public void testCreateClass() {
        SchoolClassEntity schoolClass = TestEntityData.createTestSchoolClass();
        SchoolClassEntity saved = this.schoolClassRepository.save(schoolClass);
        assertThat(saved.getId()).isPositive();
    }

    @Test
    public void testFindByClassNameAndYear() {
        this.schoolClassRepository.save(TestEntityData.createTestSchoolClass());
        Optional<SchoolClassEntity> found = this.schoolClassRepository
            .findByClassNameAndGraduationYear("11A", 2027);
        assertThat(found).isPresent();
    }

    @Test
    public void testUpdateClassYear() {
        SchoolClassEntity schoolClass = this.schoolClassRepository.save(TestEntityData.createTestSchoolClass());
        schoolClass.setGraduationYear(2028);
        this.schoolClassRepository.save(schoolClass);
        
        assertThat(this.schoolClassRepository.findById(schoolClass.getId()).get().getGraduationYear())
            .isEqualTo(2028);
    }
}

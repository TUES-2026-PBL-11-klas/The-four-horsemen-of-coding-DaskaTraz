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

import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.repositories.TeacherRepository;
import com.example.ElDnevniko.repositories.UserRepository;
import com.example.ElDnevniko.testUtils.TestEntityData;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createAndFindTeacherTest() {
        UserEntity user = TestEntityData.CreateTestUserEntity1();
        userRepository.save(user);

        TeacherEntity teacher = TestEntityData.createTestTeacher(user);

        TeacherEntity savedTeacher = teacherRepository.save(teacher);

        assertThat(savedTeacher.getId()).isPositive();
        assertThat(savedTeacher.getUser().getUsername()).isEqualTo(user.getUsername());

        Optional<TeacherEntity> found = teacherRepository.findById(savedTeacher.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getEmail()).isEqualTo(user.getEmail());
    }
}
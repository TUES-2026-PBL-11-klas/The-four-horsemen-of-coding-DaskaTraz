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

import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.repositories.StudentRepository;
import com.example.ElDnevniko.repositories.UserRepository;
import com.example.ElDnevniko.testUtils.TestEntityData;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createAndFindStudentTest() {
        UserEntity user = TestEntityData.CreateTestUserEntity1();
        userRepository.save(user);

        StudentEntity student = TestEntityData.createTestStudent(user);

        StudentEntity savedStudent = studentRepository.save(student);

        assertThat(savedStudent.getId()).isPositive();
        assertThat(savedStudent.getUser().getUsername()).isEqualTo(user.getUsername());

        Optional<StudentEntity> found = studentRepository.findById(savedStudent.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getEmail()).isEqualTo(user.getEmail());
    }
    @Test
    public void updateStudentTest()
    {
        UserEntity user = TestEntityData.CreateTestUserEntity1();
        userRepository.save(user);
        StudentEntity student = TestEntityData.createTestStudent(user);
        this.studentRepository.save(student);
        student.setNumberInClass(2);
        this.studentRepository.save(student);
        Optional<StudentEntity> newUser = this.studentRepository.findById(user.getId());
        assertThat(newUser).isPresent();
        assertThat(newUser.get().getNumberInClass()).isEqualTo(2);
    }
}
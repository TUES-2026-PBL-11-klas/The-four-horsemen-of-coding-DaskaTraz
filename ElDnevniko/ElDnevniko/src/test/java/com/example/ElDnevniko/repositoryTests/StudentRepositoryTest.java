package com.example.ElDnevniko.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.ElDnevniko.domain.entities.SchoolClassEntity;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.repositories.SchoolClassRepository;
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

    @Autowired
    private SchoolClassRepository schoolClassRepository;
    @Test
    public void createAndFindStudentTest() {
        SchoolClassEntity classE = TestEntityData.createTestSchoolClass();
        SchoolClassEntity savedClassE = this.schoolClassRepository.save(classE);
        UserEntity user = TestEntityData.CreateTestUserEntity1();
        userRepository.save(user);

        StudentEntity student = TestEntityData.createTestStudent(user, savedClassE);

        StudentEntity savedStudent = studentRepository.save(student);

        assertThat(savedStudent.getId()).isPositive();
        assertThat(savedStudent.getUser().getUsername()).isEqualTo(user.getUsername());

        Optional<StudentEntity> found = studentRepository.findById(savedStudent.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(found.get().getSchoolClass().getClassName()).isEqualTo("11A");
    }
    @Test
    public void updateStudentTest()
    {
        SchoolClassEntity classE = TestEntityData.createTestSchoolClass();
        SchoolClassEntity savedClassE = this.schoolClassRepository.save(classE);
        UserEntity user = TestEntityData.CreateTestUserEntity1();
        userRepository.save(user);
        StudentEntity student = TestEntityData.createTestStudent(user, savedClassE);
        this.studentRepository.save(student);
        student.setNumberInClass(2);
        this.studentRepository.save(student);
        Optional<StudentEntity> newUser = this.studentRepository.findById(user.getId());
        assertThat(newUser).isPresent();
        assertThat(newUser.get().getNumberInClass()).isEqualTo(2);
    }


    @Test
    public void findStudentsByClass() {
        UserEntity user = userRepository.save(TestEntityData.CreateTestUserEntity1());
        SchoolClassEntity schoolClass = schoolClassRepository.save(TestEntityData.createTestSchoolClass());

        StudentEntity student = TestEntityData.createTestStudent(user, schoolClass);
        studentRepository.save(student);

        List<StudentEntity> foundStudents = studentRepository.findAllBySchoolClass_ClassName(schoolClass.getClassName());
        
        assertThat(foundStudents).isNotEmpty();
        assertThat(foundStudents.size()).isEqualTo(1);
    }

    @Test
    public void findStudentByUsername()
    {
        UserEntity user = userRepository.save(TestEntityData.CreateTestUserEntity1());
        SchoolClassEntity schoolClass = schoolClassRepository.save(TestEntityData.createTestSchoolClass());

        StudentEntity student = TestEntityData.createTestStudent(user, schoolClass);
        studentRepository.save(student); 
        Optional<StudentEntity> found = this.studentRepository.findByUserUsername(student.getUser().getUsername());
        assertThat(found).isPresent();
        assertThat(found.get().getNumberInClass()).isEqualTo(student.getNumberInClass());
    }
}
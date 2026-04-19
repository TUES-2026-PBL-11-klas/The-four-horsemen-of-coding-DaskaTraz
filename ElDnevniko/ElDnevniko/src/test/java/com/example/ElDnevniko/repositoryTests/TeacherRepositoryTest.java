package com.example.ElDnevniko.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.ElDnevniko.domain.entities.SchoolClassEntity;
import com.example.ElDnevniko.domain.entities.SubjectEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.TeacherSubjectClassEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.repositories.SchoolClassRepository;
import com.example.ElDnevniko.repositories.SubjectRepository;
import com.example.ElDnevniko.repositories.TeacherRepository;
import com.example.ElDnevniko.repositories.TeacherSubjectClassRepository;
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

    @Autowired 
    private SubjectRepository subjectRepository;

    @Autowired 
    private SchoolClassRepository schoolClassRepository;

    @Autowired 
    private TeacherSubjectClassRepository teacherSubjectClassR;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createAndFindTeacherTest() {
        UserEntity user = TestEntityData.CreateTestUserEntity2();
        this.userRepository.save(user);

        TeacherEntity teacher = TestEntityData.createTestTeacher(user);

        TeacherEntity savedTeacher = this.teacherRepository.save(teacher);

        assertThat(savedTeacher.getId()).isPositive();
        assertThat(savedTeacher.getUser().getUsername()).isEqualTo(user.getUsername());

        Optional<TeacherEntity> found = this.teacherRepository.findById(savedTeacher.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getEmail()).isEqualTo(user.getEmail());

    }

    @Test
    public void findTeacherByUsernameTest()
    {
        UserEntity user = TestEntityData.CreateTestUserEntity2();
        this.userRepository.save(user);

        TeacherEntity teacher = TestEntityData.createTestTeacher(user);

        TeacherEntity savedTeacher = this.teacherRepository.save(teacher);

        Optional<TeacherEntity> found = this.teacherRepository.findByUserUsername(savedTeacher.getUser().getUsername());

        assertThat(found).isPresent();
    }

    @Test
    public void testTeacherWithMultipleAssignments() 
    {
        UserEntity user = this.userRepository.save(TestEntityData.CreateTestUserEntity2());
        TeacherEntity teacher = this.teacherRepository.save(TestEntityData.createTestTeacher(user));
        
        SubjectEntity math = this.subjectRepository.save(TestEntityData.createTestSubject());
        SchoolClassEntity class11A = this.schoolClassRepository.save(TestEntityData.createTestSchoolClass());
        
        SubjectEntity physics = TestEntityData.createTestSubject();
        physics.setSubjectName("physics");
        this.subjectRepository.save(physics);

        TeacherSubjectClassEntity assignment1 = TestEntityData.createTestTeacherSubjectClass(teacher, math, class11A);
        this.teacherSubjectClassR.save(assignment1);

        TeacherSubjectClassEntity assignment2 = TestEntityData.createTestTeacherSubjectClass(teacher, physics, class11A);
        this.teacherSubjectClassR.save(assignment2);

        this.entityManager.flush();
        this.entityManager.clear();

        Optional<TeacherEntity> foundTeacher = teacherRepository.findByUserUsername(user.getUsername());
        
        assertThat(foundTeacher).isPresent();
        assertThat(foundTeacher.get().getTeacherAssignments()).hasSize(2);
        
    }
}
package com.example.ElDnevniko.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
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
public class TeacherSubjectClassRepoTest {

    @Autowired 
    private TeacherSubjectClassRepository assignmentRepository;

    @Autowired 
    private TeacherRepository teacherRepository;

    @Autowired 
    private SubjectRepository subjectRepository;

    @Autowired 
    private SchoolClassRepository schoolClassRepository;

    @Autowired 
    private UserRepository userRepository;


    private TeacherEntity savedTeacher;
    private SubjectEntity savedSubject;
    private SchoolClassEntity savedClass;

    @BeforeEach
    void setUp() 
    {
        UserEntity user = this.userRepository.save(TestEntityData.CreateTestUserEntity2());
        savedTeacher = this.teacherRepository.save(TestEntityData.createTestTeacher(user));
        savedSubject = this.subjectRepository.save(TestEntityData.createTestSubject());
        savedClass = this.schoolClassRepository.save(TestEntityData.createTestSchoolClass());
    }

    @Test
    public void testCreateAssignment() {
        TeacherSubjectClassEntity assignment = TestEntityData.createTestTeacherSubjectClass(
                savedTeacher, savedSubject, savedClass);
        TeacherSubjectClassEntity saved = this.assignmentRepository.save(assignment);

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getTeacher().getId()).isEqualTo(savedTeacher.getId());
        assertThat(saved.getSubject().getSubjectName()).isEqualTo("subject1");
    }

    @Test
    public void testExistsByTeacherSubjectAndClass() {
        TeacherSubjectClassEntity assignment = TestEntityData.createTestTeacherSubjectClass(
                savedTeacher, savedSubject, savedClass);
        this.assignmentRepository.save(assignment);

        boolean exists = this.assignmentRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(
                savedTeacher.getId(), savedSubject.getId(), savedClass.getId());

        assertThat(exists).isTrue();
    }

    @Test
    public void testDeleteAssignment() {
        TeacherSubjectClassEntity assignment = this.assignmentRepository.save(
                TestEntityData.createTestTeacherSubjectClass(savedTeacher, savedSubject, savedClass));
        int id = assignment.getId();

        this.assignmentRepository.deleteById(id);

        Optional<TeacherSubjectClassEntity> found = this.assignmentRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    public void testFindAllByTeacherId() {
        SubjectEntity physics = this.subjectRepository.save(SubjectEntity.builder().subjectName("Physics").build());
        
        this.assignmentRepository.save(TestEntityData.createTestTeacherSubjectClass(savedTeacher, savedSubject, savedClass));
        this.assignmentRepository.save(TestEntityData.createTestTeacherSubjectClass(savedTeacher, physics, savedClass));

        List<TeacherSubjectClassEntity> teacherAssignments = this.assignmentRepository.findAllByTeacher_Id(savedTeacher.getId());

        assertThat(teacherAssignments).hasSize(2);
    }

    @Test
    public void findAllBySchoolClassIdTest()
    {
        SubjectEntity physics = this.subjectRepository.save(SubjectEntity.builder().subjectName("Physics").build());
        this.assignmentRepository.save(TestEntityData.createTestTeacherSubjectClass(savedTeacher, savedSubject, savedClass));
        this.assignmentRepository.save(TestEntityData.createTestTeacherSubjectClass(savedTeacher, physics, savedClass));

        List<TeacherSubjectClassEntity> teacherAssignments = this.assignmentRepository.findAllBySchoolClass_Id(savedClass.getId());

        assertThat(teacherAssignments).hasSize(2);
    }
}

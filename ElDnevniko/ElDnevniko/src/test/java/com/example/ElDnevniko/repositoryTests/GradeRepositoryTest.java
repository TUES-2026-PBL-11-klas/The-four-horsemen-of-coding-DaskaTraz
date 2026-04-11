package com.example.ElDnevniko.repositoryTests;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.ElDnevniko.domain.entities.GradeEntity;
import com.example.ElDnevniko.domain.entities.SchoolClassEntity;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.SubjectEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.repositories.GradeRepository;
import com.example.ElDnevniko.repositories.SchoolClassRepository;
import com.example.ElDnevniko.repositories.StudentRepository;
import com.example.ElDnevniko.repositories.SubjectRepository;
import com.example.ElDnevniko.repositories.TeacherRepository;
import com.example.ElDnevniko.repositories.UserRepository;
import com.example.ElDnevniko.testUtils.TestEntityData;


@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GradeRepositoryTest {
    @Autowired 
    private GradeRepository gradeRepository;

    @Autowired 
    private StudentRepository studentRepository;

    @Autowired 
    private TeacherRepository teacherRepository;

    @Autowired 
    private SubjectRepository subjectRepository;

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private SchoolClassRepository classRepository;

    private StudentEntity savedStudent;
    private TeacherEntity savedTeacher;
    private SubjectEntity savedSubject;

    @BeforeEach
    void setUp() {
        UserEntity u1 = this.userRepository.save(TestEntityData.CreateTestUserEntity1());
        SchoolClassEntity c1 = this.classRepository.save(TestEntityData.createTestSchoolClass());
        savedStudent = this.studentRepository.save(TestEntityData.createTestStudent(u1, c1));

        UserEntity u2 = this.userRepository.save(TestEntityData.CreateTestUserEntity2());
        savedTeacher = this.teacherRepository.save(TestEntityData.createTestTeacher(u2));
        savedSubject = this.subjectRepository.save(TestEntityData.createTestSubject());
    }

    @Test
    public void createGradeTest() {
        GradeEntity grade = TestEntityData.createTestGrade(savedStudent, savedTeacher, savedSubject);
        GradeEntity saved = this.gradeRepository.save(grade);
        assertThat(saved.getId()).isPositive();
    }

    @Test
    public void findGradesByStudentTest() {
        this.gradeRepository.save(TestEntityData.createTestGrade(savedStudent, savedTeacher, savedSubject));
        List<GradeEntity> grades = this.gradeRepository.findAllByStudentId(savedStudent.getId());
        assertThat(grades).hasSize(1);
    }

    @Test
    public void findGradesByTeacherTest()
    {
        this.gradeRepository.save(TestEntityData.createTestGrade(savedStudent, savedTeacher, savedSubject));
        List<GradeEntity> grades = this.gradeRepository.findAllByTeacherId(savedTeacher.getId());
        assertThat(grades).hasSize(1);
    }

    @Test
    public void findGradesByStudentAndSubjectTest()
    {
        this.gradeRepository.save(TestEntityData.createTestGrade(savedStudent, savedTeacher, savedSubject));
        List<GradeEntity> grades = this.gradeRepository.findAllByStudentIdAndSubjectId(savedStudent.getId(),
                                                                                        savedSubject.getId());
        assertThat(grades).hasSize(1);
    }


    @Test
    public void updateGradeValueTest() {
        GradeEntity grade = this.gradeRepository.save(TestEntityData.createTestGrade(savedStudent, savedTeacher, savedSubject));
        grade.setValue(2.0);
        this.gradeRepository.save(grade);
        
        assertThat(this.gradeRepository.findById(grade.getId()).get().getValue()).isEqualTo(2.0);
    }
}



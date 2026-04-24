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
import com.example.ElDnevniko.domain.entities.SubjectEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
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
public class SubjectRepositoryTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private TeacherSubjectClassRepository teacherSubjectClassR;
    
    @Test
    public void createSubjectTest() {
        SubjectEntity subject = TestEntityData.createTestSubject();
        SubjectEntity saved = this.subjectRepository.save(subject);
        assertThat(saved.getId()).isPositive();
    }

    @Test
    public void FindSubjectByNameTest() 
    {
        this.subjectRepository.save(TestEntityData.createTestSubject());
        Optional<SubjectEntity> found = this.subjectRepository.findBySubjectName("subject1");
        assertThat(found).isPresent();
    }

    @Test
    public void updateSubjectTest() {
        SubjectEntity subject = this.subjectRepository.save(TestEntityData.createTestSubject());
        subject.setSubjectName("Mathematics");
        this.subjectRepository.save(subject);
        
        assertThat(this.subjectRepository.findById(subject.getId()).get().getSubjectName())
            .isEqualTo("Mathematics");
    }

    @Test
    public void existsByNameTest() 
    {
        this.subjectRepository.save(TestEntityData.createTestSubject());
        boolean exists = this.subjectRepository.existsBySubjectName("subject1");
        assertThat(exists).isTrue();
    }

    @Test 
    public void findTeacherSubjectsTest()
    {
        UserEntity user = this.userRepository.save(TestEntityData.CreateTestUserEntity2());
        TeacherEntity teacher = this.teacherRepository.save(TestEntityData.createTestTeacher(user));
        SchoolClassEntity schoolClass = this.schoolClassRepository.save(TestEntityData.createTestSchoolClass());
        SubjectEntity subject = this.subjectRepository.save(TestEntityData.createTestSubject());
        this.teacherSubjectClassR.save(TestEntityData.createTestTeacherSubjectClass(teacher,
                                                                                    subject,
                                                                                     schoolClass));
        List<SubjectEntity> teacherSubjects = this.subjectRepository.findAllBySubjectAssignments_TeacherId(teacher.getId());
        assertThat(teacherSubjects).isNotEmpty();
        assertThat(teacherSubjects.size()).isEqualTo(1);
    }
}

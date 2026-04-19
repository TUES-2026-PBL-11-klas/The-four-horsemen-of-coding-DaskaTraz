package com.example.ElDnevniko.testUtils;

import java.util.HashSet;

import com.example.ElDnevniko.domain.entities.GradeEntity;
import com.example.ElDnevniko.domain.entities.SchoolClassEntity;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.SubjectEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.TeacherSubjectClassEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.domain.entities.UserRole;
import com.example.ElDnevniko.domain.entities.UserStatus;

public class TestEntityData 
{
    private TestEntityData(){}
    public static UserEntity CreateTestUserEntity1() {
        return UserEntity.builder().username("user1")
                                    .email("email1@gmail.com")
                                    .hashPassword("password1")
                                    .role(UserRole.STUDENT)
                                    .status(UserStatus.ACTIVE)
                                    .build();
    }

    public static UserEntity CreateTestUserEntity2() {
        return UserEntity.builder().username("user2")
                                    .email("email2@gmail.com")
                                    .hashPassword("password1")
                                    .role(UserRole.TEACHER)
                                    .status(UserStatus.ACTIVE)
                                    .build();
    }

    public static StudentEntity createTestStudent(UserEntity user,
                                                  SchoolClassEntity classE                                
    ) {
        return StudentEntity.builder().user(user)
                                    .numberInClass(1)
                                    .schoolClass(classE)
                                    .grades(new HashSet<>())
                                    .build();
    }

    public static TeacherEntity createTestTeacher(UserEntity user) 
    {
        return TeacherEntity.builder().user(user)
                            .teacherAssignments(new HashSet<>())
                            .build();
    }

    public static SubjectEntity createTestSubject() {
        return SubjectEntity.builder()
                .subjectName("subject1")
                .subjectAssignments(new HashSet<>())
                .build();
    }

    public static GradeEntity createTestGrade(StudentEntity studentE,
                                                TeacherEntity teacherE,
                                                SubjectEntity subjectE
    )
    {
        return GradeEntity.builder()
                .teacher(teacherE)
                .student(studentE)
                .subject(subjectE)
                .value(5.6)
                .build();
    }

    public static TeacherSubjectClassEntity createTestTeacherSubjectClass(TeacherEntity teacherE,
                                                                       SubjectEntity subjectE,
                                                                       SchoolClassEntity classE
    )
    {
        return TeacherSubjectClassEntity.builder()
                                        .teacher(teacherE)
                                        .subject(subjectE)
                                        .schoolClass(classE)
                                        .build();
    }

    public static SchoolClassEntity createTestSchoolClass()
    {
        return SchoolClassEntity.builder()
                                .className("11A")
                                .graduationYear(2027)
                                .classAssignments(new HashSet<>())
                                .students(new HashSet<>())
                                .build(); 
    }
}


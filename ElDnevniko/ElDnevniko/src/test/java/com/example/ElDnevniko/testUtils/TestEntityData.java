package com.example.ElDnevniko.testUtils;

import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
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

    public static StudentEntity createTestStudent(UserEntity user) {
        return StudentEntity.builder().user(user)
                                    .numberInClass(1)
                                    .build();
    }

    public static TeacherEntity createTestTeacher(UserEntity user) {
        return TeacherEntity.builder().user(user).build();
    }
}


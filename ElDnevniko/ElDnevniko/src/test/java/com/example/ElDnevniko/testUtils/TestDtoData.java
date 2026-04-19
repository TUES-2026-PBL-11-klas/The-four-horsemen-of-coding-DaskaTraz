package com.example.ElDnevniko.testUtils;

import java.util.List;

import com.example.ElDnevniko.domain.dtos.LoginRequestDto;
import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.dtos.TeacherSubjectRegisterDto;
import com.example.ElDnevniko.domain.dtos.UserRegisterDto;
import com.example.ElDnevniko.domain.entities.UserRole;

public class TestDtoData {
    private TestDtoData(){}

    public static UserRegisterDto createUserRegisterDto(UserRole role) 
    {
        return UserRegisterDto.builder()
                .username("test_user")
                .email("test@gmail.com")
                .password("password123")
                .build();
    }

    public static StudentRegisterDto createStudentProfileDto(int userId, int classId) {
        return StudentRegisterDto.builder()
                .userId(userId)
                .schoolClassId(classId)
                .build();
    }

    public static TeacherRegisterDto createTeacherProfileDto(int userId, int subjectId, int classId) {
        TeacherSubjectRegisterDto assignment = new TeacherSubjectRegisterDto(subjectId, List.of(classId));
        return TeacherRegisterDto.builder()
                .userId(userId)
                .assignments(List.of(assignment))
                .build();
    }
    public static LoginRequestDto createTestLogin()
    {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("student@gmail.com");
        dto.setPassword("password1");
        return dto;
        
    }

}

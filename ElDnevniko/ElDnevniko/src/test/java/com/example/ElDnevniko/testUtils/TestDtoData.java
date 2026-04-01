package com.example.ElDnevniko.testUtils;

import com.example.ElDnevniko.domain.dtos.LoginRequestDto;
import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.entities.UserRole;

public class TestDtoData {
    private TestDtoData(){}

    public static StudentRegisterDto createStudentDto() 
    {
        StudentRegisterDto dto = new StudentRegisterDto();
        dto.setUsername("test_student");
        dto.setEmail("student@gmail.com");
        dto.setPassword("password1");
        dto.setRole(UserRole.STUDENT);
        dto.setClassName("11A");
        dto.setNumberInClass(14);
        return dto;
    }

    public static TeacherRegisterDto createTeacherDto() {
        TeacherRegisterDto dto = new TeacherRegisterDto();
        dto.setUsername("test_teacher");
        dto.setEmail("teacher@gmail.com");
        dto.setPassword("password2");
        dto.setRole(UserRole.TEACHER);
        dto.setSubjectName("Math");
        return dto;
    }

    public static LoginRequestDto createTestLogin()
    {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("student@gmail.com");
        dto.setPassword("password1");
        return dto;
        
    }

}

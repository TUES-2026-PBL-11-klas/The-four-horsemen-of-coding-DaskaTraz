package com.example.ElDnevniko.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.ElDnevniko.domain.dtos.SchoolClassResponseDto;
import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.dtos.SubjectResponseDto;
import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.dtos.UserRegisterDto;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.UserRole;


public interface AuthService {
    public int registerUser(UserRegisterDto registerDto);
    public StudentEntity registerStudent(StudentRegisterDto registerDto);
    public TeacherEntity registerTeacher(TeacherRegisterDto registerDto);
    public void validateRegisterToken(int userId, String token);
    public String sendVerificationEmail(int userId);
    public boolean isTokenExpired(LocalDateTime createdAt);
    public List<SchoolClassResponseDto> getAllClasses();
    public List<SubjectResponseDto> getAllSubjects();
    public List<SchoolClassResponseDto> getAvailableClassesForSubject(int subjectId);
    public int chooseRole(int userId, UserRole role);
    public UserDetails loadUserByUsername(String email);
}
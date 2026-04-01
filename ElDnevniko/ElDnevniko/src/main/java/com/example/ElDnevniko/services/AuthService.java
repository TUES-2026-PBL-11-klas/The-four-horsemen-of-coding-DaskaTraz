package com.example.ElDnevniko.services;

import java.time.LocalDateTime;

import com.example.ElDnevniko.domain.dtos.LoginRequestDto;
import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.dtos.UserRegisterDto;
import com.example.ElDnevniko.domain.entities.UserEntity;

public interface AuthService {
    public void registerUser(UserRegisterDto registerDto);
    public void registerStudent(StudentRegisterDto registerDto, UserEntity user);
    public void registerTeacher(TeacherRegisterDto registerDto, UserEntity user);
    public void login(LoginRequestDto loginDto);
    public void validateRegisterToken(String email, String token);
    public void sendVerificationEmail(String email);
    public boolean isTokenExpired(LocalDateTime createdAt);
}
package com.example.ElDnevniko.domain.dtos;

import com.example.ElDnevniko.domain.entities.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class UserRegisterDto {
    private String email;
    private String username;
    private String password;
    private UserRole role;
}

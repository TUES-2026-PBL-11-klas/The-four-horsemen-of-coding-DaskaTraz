package com.example.ElDnevniko.domain.dtos;

import com.example.ElDnevniko.domain.entities.UserRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleChoiceRegisterDto {
    @NotNull(message = "User ID is required")
    private int userId;
    private UserRole userRole;
}

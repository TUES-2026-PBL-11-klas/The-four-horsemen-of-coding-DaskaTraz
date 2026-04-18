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
    private Integer userId;
    @NotNull(message = "User role is required")
    private UserRole userRole;
}

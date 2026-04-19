package com.example.ElDnevniko.domain.dtos;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherRegisterDto {
    @NotNull(message = "user Id is required")
    private Integer userId;
    @NotNull(message = "Assignments are required")
    @NotEmpty(message = "At least one subject assignment is required")
    private List<TeacherSubjectRegisterDto> assignments;
}

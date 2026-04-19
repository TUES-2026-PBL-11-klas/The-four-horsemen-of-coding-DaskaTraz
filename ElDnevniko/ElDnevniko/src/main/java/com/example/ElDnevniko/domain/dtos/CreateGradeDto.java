package com.example.ElDnevniko.domain.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGradeDto {
    @NotNull(message = "Student ID is required")
    private Integer studentId;
    
    @NotNull(message = "Subject ID is required")
    private Integer subjectId;
    
    @NotNull(message = "Grade value is required")
    @Min(value = 2, message = "Grade must be at least 2")
    @Max(value = 6, message = "Grade must be at most 6")
    private Double value;
}

package com.example.ElDnevniko.domain.dtos;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSubjectRegisterDto {
    @NotNull(message = "subject Id required")
    private int subjectId;
    @NotNull(message = "class Id required")
    private List<Integer> classIds;
}

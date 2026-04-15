package com.example.ElDnevniko.domain.dtos;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassDiaryDto {
    private SchoolClassResponseDto schoolClass;
    private String subjectName;
    private List<StudentGradeRowDto> students;
    private double classAverage;
}

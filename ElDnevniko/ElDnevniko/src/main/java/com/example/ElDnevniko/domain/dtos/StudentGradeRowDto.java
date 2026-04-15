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
public class StudentGradeRowDto {
    private int studentId;
    private String studentName;
    private int numberInClass;
    private List<GradeDto> grades;
    private double averageGrade;
}

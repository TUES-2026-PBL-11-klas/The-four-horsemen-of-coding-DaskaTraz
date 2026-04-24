package com.example.ElDnevniko.domain.dtos;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDto {
    private int id;
    private double value;
    private String subjectName;
    private String teacherName;
    private LocalDateTime date;
}

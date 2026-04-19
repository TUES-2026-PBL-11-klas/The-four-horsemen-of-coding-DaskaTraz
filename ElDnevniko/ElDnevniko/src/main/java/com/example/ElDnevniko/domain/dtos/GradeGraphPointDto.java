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
public class GradeGraphPointDto {
    private LocalDateTime date;
    private String subjectName;
    private double movingAverage;
    
}

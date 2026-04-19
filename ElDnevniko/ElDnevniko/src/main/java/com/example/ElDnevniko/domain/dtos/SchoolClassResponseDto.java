package com.example.ElDnevniko.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClassResponseDto {
    private int id;
    private String className;
    private int graduationYear;
}
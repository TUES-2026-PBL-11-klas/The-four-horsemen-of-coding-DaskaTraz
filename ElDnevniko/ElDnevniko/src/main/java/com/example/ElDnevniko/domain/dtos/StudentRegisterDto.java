package com.example.ElDnevniko.domain.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRegisterDto{
    @NotNull(message = "user Id is required")
    private Integer userId;
    @NotNull(message = "school class Id is required")
    private Integer schoolClassId;
}

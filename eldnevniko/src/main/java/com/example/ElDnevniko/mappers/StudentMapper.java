package com.example.ElDnevniko.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.entities.StudentEntity;

@Mapper(componentModel = "spring")
public interface StudentMapper{
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    StudentEntity toEntity(StudentRegisterDto studentDto);
}

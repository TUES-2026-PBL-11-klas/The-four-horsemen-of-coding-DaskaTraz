package com.example.ElDnevniko.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.entities.TeacherEntity;

@Mapper(componentModel = "spring")
public interface TeacherMapper{
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    TeacherEntity toEntity(TeacherRegisterDto teacherDto);
}

package com.example.ElDnevniko.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.entities.TeacherEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TeacherMapper{
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = ".")
    TeacherEntity toEntity(TeacherRegisterDto teacherDto);
}

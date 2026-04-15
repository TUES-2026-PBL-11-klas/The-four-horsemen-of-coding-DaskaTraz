package com.example.ElDnevniko.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ElDnevniko.domain.dtos.CreateGradeDto;
import com.example.ElDnevniko.domain.dtos.GradeDto;
import com.example.ElDnevniko.domain.entities.GradeEntity;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    @Mapping(target = "subjectName", source = "subject.subjectName")
    @Mapping(target = "teacherName", source = "teacher.user.username")
    public GradeDto toDto(GradeEntity entity);

    public List<GradeDto> toDtoList(List<GradeEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "student", ignore = true)
    public GradeEntity toEntity(CreateGradeDto dto);
}

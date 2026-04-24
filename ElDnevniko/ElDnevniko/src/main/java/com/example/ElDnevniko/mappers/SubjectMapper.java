package com.example.ElDnevniko.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.ElDnevniko.domain.dtos.SubjectResponseDto;
import com.example.ElDnevniko.domain.entities.SubjectEntity;

@Mapper(componentModel = "spring")
public interface SubjectMapper 
{
    public SubjectResponseDto toDto(SubjectEntity entity);

    public List<SubjectResponseDto> toDtoList(List<SubjectEntity> entities);
}
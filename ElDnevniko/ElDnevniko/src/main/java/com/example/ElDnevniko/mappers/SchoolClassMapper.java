package com.example.ElDnevniko.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.ElDnevniko.domain.dtos.SchoolClassResponseDto;
import com.example.ElDnevniko.domain.entities.SchoolClassEntity;

@Mapper(componentModel = "spring")
public interface SchoolClassMapper {

    public SchoolClassResponseDto toDto(SchoolClassEntity entity);

    public List<SchoolClassResponseDto> toDtoList(List<SchoolClassEntity> entities);
}
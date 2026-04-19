package com.example.ElDnevniko.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.ElDnevniko.domain.dtos.UserRegisterDto;
import com.example.ElDnevniko.domain.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hashPassword", source = "password")
    @Mapping(target = "status", constant = "PROFILE_INCOMPLETE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", constant = "NOTPROVIDED")
    public UserEntity toEntity(UserRegisterDto userDto);
} 

package com.example.ElDnevniko.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.StudentEntity;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Integer>
{
    
}

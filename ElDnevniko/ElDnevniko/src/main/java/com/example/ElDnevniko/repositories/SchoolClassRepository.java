package com.example.ElDnevniko.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.SchoolClassEntity;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClassEntity, Integer>{

    Optional<SchoolClassEntity> findByClassNameAndGraduationYear(String className, int graduationYear);

    boolean existsByClassNameAndGraduationYear(String className, int graduationYear);

    List<SchoolClassEntity> findAllByIdNotIn(List<Integer> ids);
}

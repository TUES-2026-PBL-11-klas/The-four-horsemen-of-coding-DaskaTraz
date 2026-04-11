package com.example.ElDnevniko.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.SubjectEntity;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Integer>{
    Optional<SubjectEntity> findBySubjectName(String subjectName);

    boolean existsBySubjectName(String subjectName);

    List<SubjectEntity> findAllBySubjectAssignments_TeacherId(int teacherId);
}

package com.example.ElDnevniko.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.GradeEntity;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Integer>{

    List<GradeEntity> findAllByStudentIdOrderByCreatedAtAsc(int studentId);

    List<GradeEntity> findAllByStudentIdAndSubjectIdOrderByCreatedAtAsc(int studentId, int subjectId);
    
    List<GradeEntity> findAllByTeacherId(int teacherId);

    void deleteByStudentId(int studentId);
}

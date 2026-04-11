package com.example.ElDnevniko.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.GradeEntity;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Integer>{

    List<GradeEntity> findAllByStudentId(int studentId);

    List<GradeEntity> findAllByStudentIdAndSubjectId(int studentId, int subjectId);

    List<GradeEntity> findAllByTeacherId(int teacherId);

}

package com.example.ElDnevniko.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.TeacherSubjectClassEntity;

@Repository
public interface TeacherSubjectClassRepository extends JpaRepository<TeacherSubjectClassEntity, Integer> {
    
    List<TeacherSubjectClassEntity> findAllByTeacher_Id(int teacherId);

    List<TeacherSubjectClassEntity> findAllBySchoolClass_Id(int classId);

    boolean existsByTeacherIdAndSubjectIdAndSchoolClassId(int teacherId, int subjectId, int classId);
    
    boolean existsBySubjectIdAndSchoolClassId(int subjectId, int classId);

    
}
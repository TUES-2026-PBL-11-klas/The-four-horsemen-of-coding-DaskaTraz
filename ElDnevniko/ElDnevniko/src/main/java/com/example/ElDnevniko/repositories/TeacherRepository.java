package com.example.ElDnevniko.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity, Integer>
{
    Optional<TeacherEntity> findByUserUsername(String username);

    Optional<TeacherEntity> findByUser(UserEntity user);

    List<TeacherEntity> findAllByTeacherAssignments_SubjectId(int subjectId);
}

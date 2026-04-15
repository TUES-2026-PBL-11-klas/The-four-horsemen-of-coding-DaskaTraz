package com.example.ElDnevniko.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Integer>
{
    Optional<StudentEntity> findByUserUsername(String username);

    Optional<StudentEntity> findByUser(UserEntity user);

    List<StudentEntity> findAllBySchoolClass_ClassName(String className);

    List<StudentEntity> findAllBySchoolClassIdOrderByNumberInClassAsc(int classId);
    
    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.schoolClass.id = :classId " +
       "AND s.user.status = 'ACTIVE' AND s.user.username < :username")
    int countActiveBeforeAlphabetically(int classId, String username);

    @Modifying
    @Query("UPDATE StudentEntity s SET s.numberInClass = s.numberInClass + 1 " +
        "WHERE s.schoolClass.id = :classId AND s.user.status = 'ACTIVE' AND s.user.username > :username")
    void incrementActiveNumbersAfter(int classId, String username);
}

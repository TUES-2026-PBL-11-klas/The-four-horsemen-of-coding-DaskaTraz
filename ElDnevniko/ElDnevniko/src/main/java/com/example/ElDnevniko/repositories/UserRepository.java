package com.example.ElDnevniko.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.domain.entities.UserStatus;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>{
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<UserEntity> findByStatusInAndCreatedAtBefore(List<UserStatus> statuses, LocalDateTime date);
    void deleteByStatusAndCreatedAtBefore(UserStatus status, LocalDateTime dateTime);
}

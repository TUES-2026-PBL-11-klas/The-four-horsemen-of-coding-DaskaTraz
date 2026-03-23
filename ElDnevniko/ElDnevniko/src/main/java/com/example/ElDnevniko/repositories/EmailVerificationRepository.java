package com.example.ElDnevniko.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ElDnevniko.domain.entities.EmailVerificationEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Integer>
{
    Optional<EmailVerificationEntity> findByUser(UserEntity user);    
}

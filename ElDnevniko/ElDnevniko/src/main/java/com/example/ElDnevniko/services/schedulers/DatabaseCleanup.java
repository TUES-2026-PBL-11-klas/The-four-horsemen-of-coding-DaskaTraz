package com.example.ElDnevniko.services.schedulers;

import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.domain.entities.UserStatus;
import com.example.ElDnevniko.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseCleanup {

    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private EmailVerificationRepository emailValidationRepo;
    private GradeRepository gradeRepository;
    private TeacherSubjectClassRepository teacherSubjectClassR;

    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional
    public void cleanupUnverifiedUsers() 
    {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(7);
        List<UserStatus> targetStatuses = Arrays.asList(UserStatus.NOTVERIFIED, UserStatus.PROFILE_INCOMPLETE);
        List<UserEntity> usersToDelete = userRepository.findByStatusInAndCreatedAtBefore(targetStatuses, thresholdDate);
        if(usersToDelete.isEmpty()) 
        {
            return;
        }
        for(UserEntity user : usersToDelete) 
        {
            deleteUserCompletely(user);
        }

    }

    private void deleteUserCompletely(UserEntity user) 
    {
        emailValidationRepo.findByUser(user).ifPresent(emailValidationRepo::delete);
        studentRepository.findByUser(user).ifPresent(student -> 
        {
            gradeRepository.deleteByStudentId(student.getId());
            studentRepository.delete(student);        
        });

        teacherRepository.findByUser(user).ifPresent(teacher -> 
        {
            teacherSubjectClassR.deleteByTeacherId(teacher.getId());
            teacherRepository.delete(teacher);
        });

        userRepository.delete(user);
    }
}
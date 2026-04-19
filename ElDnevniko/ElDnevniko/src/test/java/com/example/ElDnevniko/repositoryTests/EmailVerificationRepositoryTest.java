package com.example.ElDnevniko.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.ElDnevniko.domain.entities.EmailVerificationEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.repositories.EmailVerificationRepository;
import com.example.ElDnevniko.repositories.UserRepository;
import com.example.ElDnevniko.testUtils.TestEntityData;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EmailVerificationRepositoryTest {

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createAndFindEmailVerificationTest() {
        UserEntity user = TestEntityData.CreateTestUserEntity1();
        userRepository.save(user);

        EmailVerificationEntity verification = new EmailVerificationEntity(user);
        
        emailVerificationRepository.save(verification);

        Optional<EmailVerificationEntity> found = emailVerificationRepository.findById(verification.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(found.get().isVerified()).isFalse();
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    public void updateVerificationStatusTest() {
        UserEntity user = TestEntityData.CreateTestUserEntity1();
        userRepository.save(user);

        EmailVerificationEntity verification = new EmailVerificationEntity(user);
        emailVerificationRepository.save(verification);

        verification.setVerified(true);
        emailVerificationRepository.save(verification);

        Optional<EmailVerificationEntity> updated = emailVerificationRepository.findById(verification.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().isVerified()).isTrue();
    }
}
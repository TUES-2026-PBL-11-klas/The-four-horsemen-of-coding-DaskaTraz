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

import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.repositories.UserRepository;
import com.example.ElDnevniko.testUtils.TestEntityData;

@ActiveProfiles("test")
@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void createAndFindUserTest()
    {
        UserEntity user1 = TestEntityData.CreateTestUserEntity1();
        UserEntity savedUser = this.userRepository.save(user1);
        int generatedId = savedUser.getId();
        assertThat(savedUser).isNotNull();
        assertThat(generatedId).isEqualTo(1);
        Optional<UserEntity> findUser = this.userRepository.findById(generatedId);
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getUsername()).isEqualTo(user1.getUsername());
    }

    @Test
    public void findUserByEmailAndUsernameTest()
    {
        UserEntity user1 = TestEntityData.CreateTestUserEntity1();
        this.userRepository.save(user1);
        String username = user1.getUsername();
        String email = user1.getEmail();
        Optional<UserEntity> userWithThisUsername = this.userRepository.findByUsername(username);
        assertThat(userWithThisUsername).isPresent();
        assertThat(userWithThisUsername.get()).isEqualTo(user1);
        Optional<UserEntity> userWithThisEmail = this.userRepository.findByEmail(email);
        assertThat(userWithThisEmail).isPresent();
        assertThat(userWithThisEmail.get()).isEqualTo(user1);
    }

    @Test
    public void updateUserTest()
    {
        UserEntity user1 = TestEntityData.CreateTestUserEntity1();
        this.userRepository.save(user1);
        String newEmail = "newEmail@gmail.com";
        user1.setEmail(newEmail);
        this.userRepository.save(user1);
        Optional<UserEntity> userWithThatEmail = this.userRepository.findByEmail(newEmail);
        assertThat(userWithThatEmail).isPresent();
        assertThat(userWithThatEmail.get()).isEqualTo(user1);
    }
    @Test
    public void existByUserOrEmailTest()
    {
        UserEntity user1 = TestEntityData.CreateTestUserEntity1();
        this.userRepository.save(user1);
        assertThat(this.userRepository.existsByEmail(user1.getEmail())).isTrue();
        assertThat(this.userRepository.existsByUsername(user1.getUsername())).isTrue();
    }
   
}

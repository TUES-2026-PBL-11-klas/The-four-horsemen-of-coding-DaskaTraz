package com.example.ElDnevniko.mapperTests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.dtos.UserRegisterDto;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.domain.entities.UserRole;
import com.example.ElDnevniko.mappers.UserMapper;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserRegisterDtoToEntity() {
        UserRegisterDto dto = new StudentRegisterDto();
        dto.setEmail("test@gmail.com");
        dto.setUsername("testuser");
        dto.setPassword("rawPassword123");
        dto.setRole(UserRole.STUDENT);

        UserEntity entity = this.userMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getUsername()).isEqualTo(dto.getUsername());
        assertThat(entity.getHashPassword()).isEqualTo("rawPassword123");
        assertThat(entity.getRole()).isEqualTo(UserRole.STUDENT);
    }
}

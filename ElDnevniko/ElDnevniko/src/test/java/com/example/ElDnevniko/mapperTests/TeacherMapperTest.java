package com.example.ElDnevniko.mapperTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.UserStatus;
import com.example.ElDnevniko.mappers.TeacherMapper;
import com.example.ElDnevniko.testUtils.TestDtoData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class TeacherMapperTest {

    @Autowired
    private TeacherMapper teacherMapper;

    @Test
    public void testTeacherRegisterDtoToEntity() {
        TeacherRegisterDto dto = TestDtoData.createTeacherDto();

        TeacherEntity entity = this.teacherMapper.toEntity(dto);

        assertThat(entity).isNotNull();

        assertThat(entity.getUser()).isNotNull();
        assertThat(entity.getUser().getUsername()).isEqualTo(dto.getUsername());
        assertThat(entity.getUser().getStatus()).isEqualTo(UserStatus.NOTVERIFIED);
    }
}
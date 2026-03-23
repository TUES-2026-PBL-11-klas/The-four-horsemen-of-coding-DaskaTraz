package com.example.ElDnevniko.mapperTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.UserStatus;
import com.example.ElDnevniko.mappers.StudentMapper;
import com.example.ElDnevniko.testUtils.TestDtoData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class StudentMapperTest {

    @Autowired
    private StudentMapper studentMapper;

    @Test
    public void testStudentRegisterDtoToEntity() {
        StudentRegisterDto dto = TestDtoData.createStudentDto();

        StudentEntity entity = this.studentMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getNumberInClass()).isEqualTo(14);

        assertThat(entity.getUser()).isNotNull();
        assertThat(entity.getUser().getUsername()).isEqualTo(dto.getUsername());
        assertThat(entity.getUser().getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getUser().getHashPassword()).isEqualTo(dto.getPassword());
        assertThat(entity.getUser().getStatus()).isEqualTo(UserStatus.NOTVERIFIED);
    }
}
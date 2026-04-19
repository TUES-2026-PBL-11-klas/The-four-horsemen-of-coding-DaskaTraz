package com.example.ElDnevniko.services;



import java.util.List;

import com.example.ElDnevniko.domain.dtos.ClassDiaryDto;
import com.example.ElDnevniko.domain.dtos.CreateGradeDto;
import com.example.ElDnevniko.domain.dtos.GradeDto;
import com.example.ElDnevniko.domain.dtos.SchoolClassResponseDto;
import com.example.ElDnevniko.domain.dtos.StudentGradeRowDto;
import com.example.ElDnevniko.domain.dtos.SubjectResponseDto;

public interface TeacherService {

    public List<SubjectResponseDto> getTeacherSubjects(String email);
    public List<SchoolClassResponseDto> getTeacherClassesForSubject(String email, int subjectId);
    public StudentGradeRowDto getStudentGradesAtSubject(int studentId, int subjectId);
    public ClassDiaryDto getClassGradesAtSubject(String email, int classId, int subjectId);
    public GradeDto addGrade(String email, CreateGradeDto createGradeDto);
    public GradeDto updateGrade(String email, int gradeId, double value);
} 

package com.example.ElDnevniko.services;

import java.util.List;


import com.example.ElDnevniko.domain.dtos.GradeGraphPointDto;
import com.example.ElDnevniko.domain.dtos.StudentGradeRowDto;
import com.example.ElDnevniko.domain.dtos.SubjectResponseDto;

public interface StudentService {
    public StudentGradeRowDto getStudentGradesAtSubject(String email, int subjectId);
    public List<StudentGradeRowDto> getStudentGradesForAllSubjects(String email);
    public List<SubjectResponseDto> getStudentSubjects(String email);
    public List<GradeGraphPointDto> getStudentGradeTrends(String email, int subjectId); 
    public void sendEmailForLowGrades(String email, String subjectName);
    public boolean isStudentGradeLow(String email, String subjectName);
    
}

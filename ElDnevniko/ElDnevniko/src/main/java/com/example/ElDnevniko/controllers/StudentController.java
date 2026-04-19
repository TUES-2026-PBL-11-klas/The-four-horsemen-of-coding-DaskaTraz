package com.example.ElDnevniko.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ElDnevniko.domain.dtos.ApiResponse;
import com.example.ElDnevniko.domain.dtos.GradeGraphPointDto;
import com.example.ElDnevniko.domain.dtos.StudentGradeRowDto;
import com.example.ElDnevniko.domain.dtos.SubjectResponseDto;
import com.example.ElDnevniko.services.StudentService;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    
    @Autowired
    private StudentService studentService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<List<StudentGradeRowDto>>> getGrades() {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(ApiResponse.success("Grades retrieved successfully",
                                                    studentService.getStudentGradesForAllSubjects(email)));
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getSubjects() 
    {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved successfully",
                                                    studentService.getStudentSubjects(email)));
    }

    @GetMapping("/subjects/{subjectId}/grade-graph")
    public ResponseEntity<ApiResponse<List<GradeGraphPointDto>>> getGradeTrends(@PathVariable int subjectId) 
    {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(ApiResponse.success("Grade trends retrieved successfully",
                                                    studentService.getStudentGradeTrends(email, subjectId)));
    }



    
}

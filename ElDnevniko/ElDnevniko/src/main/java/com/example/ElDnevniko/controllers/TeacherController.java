package com.example.ElDnevniko.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.example.ElDnevniko.domain.dtos.ApiResponse;
import com.example.ElDnevniko.domain.dtos.ClassDiaryDto;
import com.example.ElDnevniko.domain.dtos.CreateGradeDto;
import com.example.ElDnevniko.domain.dtos.GradeDto;
import com.example.ElDnevniko.domain.dtos.SchoolClassResponseDto;
import com.example.ElDnevniko.domain.dtos.SubjectResponseDto;
import com.example.ElDnevniko.services.TeacherService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;


    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getSubjects() 
    {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved successfully",
                                                    teacherService.getTeacherSubjects(email)));
    }

    @GetMapping("/subjects/{subjectId}/classes")
    public ResponseEntity<ApiResponse<List<SchoolClassResponseDto>>> getClasses(@PathVariable int subjectId) 
    {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(ApiResponse.success("Classes retrieved successfully", 
                                                    teacherService.getTeacherClassesForSubject(email, subjectId)));
    }

    @GetMapping("/classes/{classId}/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<ClassDiaryDto>> getClassDiary(@PathVariable int classId,
                                                                    @PathVariable int subjectId) 
    {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(ApiResponse.success("Class diary retrieved successfully", 
                                teacherService.getClassGradesAtSubject(email, classId, subjectId)));
    }

    @PostMapping("/grades")
    public ResponseEntity<ApiResponse<GradeDto>> addGrade(@Valid @RequestBody CreateGradeDto createGradeDto) 
    {
        String email = getCurrentUserEmail();
        return ResponseEntity.status(201).body(ApiResponse.success("Grade added successfully", 
                                                teacherService.addGrade(email, createGradeDto)));
    }

    @PatchMapping("/grades/{gradeId}")
    public ResponseEntity<ApiResponse<GradeDto>> updateGrade(@PathVariable int gradeId,
                                                            @RequestBody double newValue) 
    {
        String email = getCurrentUserEmail();
        return ResponseEntity.ok(ApiResponse.success("Grade updated successfully", 
                                                teacherService.updateGrade(email, gradeId, newValue)));
    }
}   


package com.example.ElDnevniko.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.ElDnevniko.domain.dtos.GradeDto;
import com.example.ElDnevniko.domain.dtos.GradeGraphPointDto;
import com.example.ElDnevniko.domain.dtos.StudentGradeRowDto;
import com.example.ElDnevniko.domain.dtos.SubjectResponseDto;
import com.example.ElDnevniko.domain.entities.GradeEntity;
import com.example.ElDnevniko.domain.entities.SchoolClassEntity;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.SubjectEntity;
import com.example.ElDnevniko.exceptions.EmailSendException;
import com.example.ElDnevniko.exceptions.SchoolClassNotFoundException;
import com.example.ElDnevniko.exceptions.SubjectNotFoundException;
import com.example.ElDnevniko.exceptions.SubjectNotPresentInClassException;
import com.example.ElDnevniko.exceptions.TeacherAccessDeniedException;
import com.example.ElDnevniko.exceptions.UserNotFoundException;
import com.example.ElDnevniko.mappers.GradeMapper;
import com.example.ElDnevniko.mappers.SubjectMapper;
import com.example.ElDnevniko.repositories.GradeRepository;
import com.example.ElDnevniko.repositories.SchoolClassRepository;
import com.example.ElDnevniko.repositories.StudentRepository;
import com.example.ElDnevniko.repositories.SubjectRepository;
import com.example.ElDnevniko.repositories.TeacherSubjectClassRepository;
import com.example.ElDnevniko.services.StudentService;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class StudentServiceImpl implements StudentService{
    private StudentRepository studentRepository;
    private GradeRepository gradeRepository;
    private SubjectRepository subjectRepository;
    private SchoolClassRepository schoolClassRepository;
    private TeacherSubjectClassRepository tscRepository;
    private GradeMapper gradeMapper;
    private SubjectMapper subjectMapper;
    private JavaMailSender mailSender;



    public StudentServiceImpl(StudentRepository studentRepository, 
                            GradeRepository gradeRepository, 
                            SubjectRepository subjectRepository, 
                            SchoolClassRepository schoolClassRepository,
                            TeacherSubjectClassRepository tscRepository,
                            GradeMapper gradeMapper,
                            SubjectMapper subjectMapper,
                            JavaMailSender mailSender) 
    {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.subjectRepository = subjectRepository;
        this.gradeMapper = gradeMapper;
        this.subjectMapper = subjectMapper;
        this.schoolClassRepository = schoolClassRepository;
        this.tscRepository = tscRepository;
        this.mailSender = mailSender;
    }


    @Override
    public StudentGradeRowDto getStudentGradesAtSubject(String email, int subjectId) 
    {
        StudentEntity student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Student not found"));
        validateStudentAuthority(email);
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        SchoolClassEntity schoolClass = this.schoolClassRepository.findById(student.getSchoolClass().getId())
            .orElseThrow(() -> new SchoolClassNotFoundException("Class not found"));
        if(!this.tscRepository.existsBySubjectIdAndSchoolClassId(subjectId, schoolClass.getId()))
        {
            throw new SubjectNotPresentInClassException("This student does not attend this subject.");
        }
        List<GradeEntity> grades = gradeRepository.findAllByStudentIdAndSubjectIdOrderByCreatedAtAsc(student.getId(), subjectId);

        double average = grades.stream()
                .mapToDouble(GradeEntity::getValue)
                .average()
                .orElse(2.0);
        StudentGradeRowDto studentGradeRowDto = StudentGradeRowDto.builder()
                .studentId(student.getId())
                .studentName(student.getUser().getUsername())
                .subjectName(subject.getSubjectName())
                .numberInClass(student.getNumberInClass())
                .averageGrade(average)
                .grades(gradeMapper.toDtoList(grades))
                .build();
        return studentGradeRowDto;
    }
    
    @Override
    public List<StudentGradeRowDto> getStudentGradesForAllSubjects(String email) 
    {
        StudentEntity student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Student not found"));
        validateStudentAuthority(email);
        List<SubjectEntity> subjects = subjectRepository.findAllBySubjectAssignments_SchoolClassId(
            student.getSchoolClass().getId()
        );
        List<StudentGradeRowDto> studentGradeRowDtos = new ArrayList<>();
        for(SubjectEntity subject : subjects)
        {
            StudentGradeRowDto studentGradeRowDto = getStudentGradesAtSubject(email, subject.getId());
            studentGradeRowDtos.add(studentGradeRowDto);
        }
        return studentGradeRowDtos;
    }

    @Override
    public List<SubjectResponseDto> getStudentSubjects(String email) 
    {
        StudentEntity student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Student not found"));
        validateStudentAuthority(email);
        List<SubjectEntity> subjects = subjectRepository.findAllBySubjectAssignments_SchoolClassId(
            student.getSchoolClass().getId()
        );
        return subjectMapper.toDtoList(subjects);
    }

    @Override
    public List<GradeGraphPointDto> getStudentGradeTrends(String email, int subjectId) 
    {
        validateStudentAuthority(email);
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        StudentGradeRowDto studentGradeRowDto = getStudentGradesAtSubject(email, subjectId);
        
        List<GradeGraphPointDto> gradeGraph = new ArrayList<>();
        int gradeCounter = 1;
        double gradeSum = 0.0;
        for(GradeDto grade : studentGradeRowDto.getGrades())
        {
            gradeSum += grade.getValue();
            double point = gradeSum / gradeCounter;
            gradeCounter++;
            GradeGraphPointDto graphPoint = GradeGraphPointDto.builder()
                    .movingAverage(point)
                    .subjectName(subject.getSubjectName())
                    .date(grade.getDate())
                    .build();
            gradeGraph.add(graphPoint);
        }
        return gradeGraph;
    }

    @Override
    public void sendEmailForLowGrades(String email, String subjectName) 
    {
        StudentEntity student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Student not found"));
        SubjectEntity subject = subjectRepository.findBySubjectName(subjectName)
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        try
        {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(new InternetAddress("kaloian3141@gmail.com", "Dazkatraz"));
            helper.setTo(email);
            helper.setSubject("Attention - Dazkatraz");
            helper.setText("<h2>Your " + subject.getSubjectName() + " grade is low</h2>" +
                "<p> Please review your performance in " + subject.getSubjectName() + " </p>", true);
            this.mailSender.send(mimeMessage);
        }
        catch(Exception e)
        {
            throw new EmailSendException("Fail to send email to " + student.getUser().getEmail() + ":", e);
        }
    }

    @Override
    public boolean isStudentGradeLow(String email, String subjectName) 
    {
        StudentGradeRowDto studentGradeRowDto = getStudentGradesAtSubject(email, subjectRepository.findBySubjectName(subjectName)
            .orElseThrow(() -> new SubjectNotFoundException("Subject not found")).getId());
        return studentGradeRowDto.getAverageGrade() < 3.0;
    }
    private void validateStudentAuthority(String email) 
    {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        StudentEntity student = studentRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Authenticated student not found"));

        if(!student.getUser().getEmail().equals(email))
        {
            throw new TeacherAccessDeniedException("You are not authorized to access data for another teacher.");
        }
    }
}

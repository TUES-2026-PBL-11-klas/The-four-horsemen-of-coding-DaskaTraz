package com.example.ElDnevniko.services.impl;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.ElDnevniko.domain.dtos.ClassDiaryDto;
import com.example.ElDnevniko.domain.dtos.CreateGradeDto;
import com.example.ElDnevniko.domain.dtos.GradeDto;
import com.example.ElDnevniko.domain.dtos.SchoolClassResponseDto;
import com.example.ElDnevniko.domain.dtos.StudentGradeRowDto;
import com.example.ElDnevniko.domain.dtos.SubjectResponseDto;
import com.example.ElDnevniko.domain.entities.GradeEntity;
import com.example.ElDnevniko.domain.entities.SchoolClassEntity;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.SubjectEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.TeacherSubjectClassEntity;
import com.example.ElDnevniko.domain.entities.UserStatus;
import com.example.ElDnevniko.exceptions.AccountNotActivatedException;
import com.example.ElDnevniko.exceptions.GradeNotFoundException;
import com.example.ElDnevniko.exceptions.InvalidUserDataException;
import com.example.ElDnevniko.exceptions.SchoolClassNotFoundException;
import com.example.ElDnevniko.exceptions.SubjectNotFoundException;
import com.example.ElDnevniko.exceptions.SubjectNotPresentInClassException;
import com.example.ElDnevniko.exceptions.TeacherAccessDeniedException;
import com.example.ElDnevniko.exceptions.UserNotFoundException;
import com.example.ElDnevniko.mappers.GradeMapper;
import com.example.ElDnevniko.mappers.SchoolClassMapper;
import com.example.ElDnevniko.mappers.SubjectMapper;
import com.example.ElDnevniko.repositories.GradeRepository;
import com.example.ElDnevniko.repositories.SchoolClassRepository;
import com.example.ElDnevniko.repositories.StudentRepository;
import com.example.ElDnevniko.repositories.SubjectRepository;
import com.example.ElDnevniko.repositories.TeacherRepository;
import com.example.ElDnevniko.repositories.TeacherSubjectClassRepository;
import com.example.ElDnevniko.services.StudentService;
import com.example.ElDnevniko.services.TeacherService;
import jakarta.transaction.Transactional;

@Service
public class TeacherServiceImpl implements TeacherService 
{
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private SubjectRepository subjectRepository;
    private SchoolClassRepository schoolClassRepository;
    private GradeRepository gradeRepository;
    private TeacherSubjectClassRepository tscRepository;
    private GradeMapper gradeMapper;
    private SchoolClassMapper schoolClassMapper;
    private SubjectMapper subjectMapper;
    private StudentService studentService;

    public TeacherServiceImpl(
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            SchoolClassRepository schoolClassRepository,
            GradeRepository gradeRepository,
            TeacherSubjectClassRepository tscRepository,
            GradeMapper gradeMapper,
            SchoolClassMapper schoolClassMapper,
            SubjectMapper subjectMapper,
            StudentService studentService
        ) 
        {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.gradeRepository = gradeRepository;
        this.tscRepository = tscRepository;
        this.gradeMapper = gradeMapper;
        this.schoolClassMapper = schoolClassMapper;
        this.subjectMapper = subjectMapper;
        this.studentService = studentService;
    }


    @Override
    public List<SubjectResponseDto> getTeacherSubjects(String email) 
    {
        validateTeacherAuthority(email);
        TeacherEntity teacher = teacherRepository.findByUserEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Teacher not found"));
        List<TeacherSubjectClassEntity> assignments = tscRepository.findAllByTeacher_Id(teacher.getId());
        List<SubjectEntity> subjects = assignments.stream()
                .map(TeacherSubjectClassEntity::getSubject)
                .distinct()
                .toList();
        return this.subjectMapper.toDtoList(subjects);    
    }

    @Override
    public List<SchoolClassResponseDto> getTeacherClassesForSubject(String email, int subjectId) 
    {
        validateTeacherAuthority(email);
        TeacherEntity teacher = teacherRepository.findByUserEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Teacher not found"));
        if(!subjectRepository.existsById(subjectId))
        {
            throw new SubjectNotFoundException("Subject not found");
        }
        List<TeacherSubjectClassEntity> assignments = tscRepository.findAllByTeacher_IdAndSubject_Id(teacher.getId(), subjectId);
        List<SchoolClassEntity> classes = assignments.stream()
                .map(TeacherSubjectClassEntity::getSchoolClass)
                .distinct()
                .toList();
        return this.schoolClassMapper.toDtoList(classes);
    }

    @Override
    public StudentGradeRowDto getStudentGradesAtSubject(int studentId, int subjectId) 
    {
        StudentEntity student = this.studentRepository.findById(studentId)
            .orElseThrow(() -> new UserNotFoundException("Student not found"));
        if(!student.getUser().getStatus().equals(UserStatus.ACTIVE))
        {
            throw new AccountNotActivatedException("Student account is not activated");
        }

        if(!this.subjectRepository.existsById(subjectId))
        {
            throw new SubjectNotFoundException("Subject not found");
        }
        SchoolClassEntity schoolClass = this.schoolClassRepository.findById(student.getSchoolClass().getId())
            .orElseThrow(() -> new SchoolClassNotFoundException("Class not found"));
        if(!this.tscRepository.existsBySubjectIdAndSchoolClassId(subjectId, schoolClass.getId()))
        {
            throw new SubjectNotPresentInClassException("This student does not attend this subject.");
        }
        List<GradeEntity> grades = gradeRepository.findAllByStudentIdAndSubjectIdOrderByCreatedAtAsc(studentId, subjectId);

        double average = grades.stream()
                .mapToDouble(GradeEntity::getValue)
                .average()
                .orElse(2.0);
        StudentGradeRowDto studentGradeRowDto = StudentGradeRowDto.builder()
                .studentId(studentId)
                .studentName(student.getUser().getUsername())
                .numberInClass(student.getNumberInClass())
                .averageGrade(average)
                .grades(gradeMapper.toDtoList(grades))
                .build();
        return studentGradeRowDto;
    }

    @Transactional
    @Override
    public ClassDiaryDto getClassGradesAtSubject(String email, int classId, int subjectId) 
    {
        validateTeacherAuthority(email);
        TeacherEntity teacher = teacherRepository.findByUserEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Teacher not found"));
        
        SchoolClassEntity schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new SchoolClassNotFoundException("Class not found"));
        
        SubjectEntity subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));

        boolean hasAccess = tscRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(
            teacher.getId(), subjectId, classId);
        if(!hasAccess) 
            throw new TeacherAccessDeniedException("You do not have permission to view this class for this subject."); 

        List<StudentEntity> students = this.studentRepository
            .findAllBySchoolClassIdOrderByNumberInClassAsc(classId);
        
        List<StudentGradeRowDto> studentRows = students.stream()
            .filter(s -> s.getUser().getStatus().equals(UserStatus.ACTIVE))
            .map(student -> getStudentGradesAtSubject(student.getId(), subjectId))
            .toList();

        double classAvg = studentRows.stream()
            .mapToDouble(StudentGradeRowDto::getAverageGrade)
            .average()
            .orElse(2.0);

        return ClassDiaryDto.builder()
                .schoolClass(schoolClassMapper.toDto(schoolClass))
                .subjectName(subject.getSubjectName())
                .students(studentRows)
                .classAverage(classAvg)
                .build();

    }

    @Override
    public GradeDto addGrade(String email, CreateGradeDto createGradeDto) 
    {         
        validateTeacherAuthority(email);
        TeacherEntity teacher = teacherRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Teacher not found"));
        StudentEntity student = studentRepository.findById(createGradeDto.getStudentId())
                .orElseThrow(() -> new UserNotFoundException("Student not found"));
        SubjectEntity subject = subjectRepository.findById(createGradeDto.getSubjectId())
                .orElseThrow(() -> new SubjectNotFoundException("Subject not found"));
        if(!this.tscRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(teacher.getId(), createGradeDto.getSubjectId(), student.getSchoolClass().getId()))
        {
            throw new TeacherAccessDeniedException("You do not have permission to add a grade for this student in this subject.");
        }
        if(!student.getUser().getStatus().equals(UserStatus.ACTIVE))
        {
            throw new AccountNotActivatedException("Student account is not activated");
        }

        GradeEntity newGrade = gradeMapper.toEntity(createGradeDto);
        newGrade.setTeacher(teacher);
        newGrade.setStudent(student);
        newGrade.setSubject(subject);
        newGrade.setCreatedAt(LocalDateTime.now());
        GradeEntity savedGrade = gradeRepository.save(newGrade);
        if(this.studentService.isStudentGradeLow(student.getUser().getEmail(), subject.getSubjectName()))
        {
            this.studentService.sendEmailForLowGrades(student.getUser().getEmail(), subject.getSubjectName());
        }
        return gradeMapper.toDto(savedGrade);
    }
    @Override
    public GradeDto updateGrade(String email, int gradeId, double value) 
    {
        validateTeacherAuthority(email);
        TeacherEntity teacher = teacherRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Teacher not found"));
        if(!teacherRepository.existsById(teacher.getId()))
        {
            throw new UserNotFoundException("Teacher not found");
        }
        GradeEntity grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new GradeNotFoundException("Grade not found")); 
        if(value < 2.0 || value > 6.0)
        {
            throw new InvalidUserDataException("Grade value must be between 2 and 6");
        }

        if(!this.tscRepository.existsByTeacherIdAndSubjectIdAndSchoolClassId(teacher.getId(), grade.getSubject().getId(), grade.getStudent().getSchoolClass().getId()))
        {
            throw new TeacherAccessDeniedException("You do not have permission to update a grade for this student in this subject.");
        }
        grade.setValue(value);
        GradeEntity updatedGrade = gradeRepository.save(grade);
        if(this.studentService.isStudentGradeLow(grade.getStudent().getUser().getEmail(), grade.getSubject().getSubjectName()))
        {
            this.studentService.sendEmailForLowGrades(grade.getStudent().getUser().getEmail(), grade.getSubject().getSubjectName());
        }
        return gradeMapper.toDto(updatedGrade);
    }
    
    private void validateTeacherAuthority(String email) 
    {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        TeacherEntity teacher = teacherRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Authenticated teacher not found"));

        if(!teacher.getUser().getEmail().equals(email))
        {
            throw new TeacherAccessDeniedException("You are not authorized to access data for another teacher.");
        }
    }
    
}

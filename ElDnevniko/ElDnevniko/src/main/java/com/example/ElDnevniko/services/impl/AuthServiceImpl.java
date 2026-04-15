package com.example.ElDnevniko.services.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.userdetails.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ElDnevniko.domain.dtos.*;
import com.example.ElDnevniko.domain.entities.*;
import com.example.ElDnevniko.exceptions.*;
import com.example.ElDnevniko.mappers.*;
import com.example.ElDnevniko.repositories.*;
import com.example.ElDnevniko.services.AuthService;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
@Service
public class AuthServiceImpl implements AuthService, UserDetailsService
{
    
    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private EmailVerificationRepository emailValidationRepo;
    private JavaMailSender javaMailSender;
    private SchoolClassRepository schoolClassRepository;
    private SubjectRepository subjectRepository;
    private TeacherSubjectClassRepository teacherSubjectClassR;
    private SubjectMapper subjectMapper;
    private SchoolClassMapper schoolClassMapper;
    
    public AuthServiceImpl(
        UserRepository userRepository,
        StudentRepository studentRepository,
        TeacherRepository teacherRepository,
        UserMapper userMapper,
        SubjectMapper subjectMapper,
        SchoolClassMapper schoolClassMapper,
        PasswordEncoder passwordEncoder,
        EmailVerificationRepository emailVR,
        JavaMailSender javaMS,
        SchoolClassRepository schoolClassRepository,
        SubjectRepository subjectRepository,
        TeacherSubjectClassRepository teacherSubjectClassR
    )
    {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailValidationRepo = emailVR;
        this.javaMailSender = javaMS;
        this.schoolClassRepository = schoolClassRepository;
        this.subjectRepository = subjectRepository;
        this.teacherSubjectClassR = teacherSubjectClassR;
        this.schoolClassMapper = schoolClassMapper;
        this.subjectMapper = subjectMapper;
    }

    @Transactional
    @Override
    public int registerUser(UserRegisterDto registerDto)
    {
        this.userRepository.findByEmail(registerDto.getEmail())
        .or(() -> this.userRepository.findByUsername(registerDto.getUsername()))
        .ifPresent(existingUser -> 
        {
            
            if(existingUser.getStatus() == UserStatus.ACTIVE) 
            {
                throw new UserAlreadyExistException("Account with that name or email already exist");
            }

            
            if(existingUser.getStatus() == UserStatus.NOTVERIFIED) 
            {
                this.emailValidationRepo.findByUser(existingUser)
                        .ifPresent(ev -> this.emailValidationRepo.delete(ev));
                
                this.studentRepository.findByUser(existingUser)
                        .ifPresent(student -> this.studentRepository.delete(student));
                
                this.teacherRepository.findByUser(existingUser)
                        .ifPresent(teacher -> this.teacherRepository.delete(teacher));
            }
            
            this.userRepository.delete(existingUser);
            this.userRepository.flush();
        });
        UserEntity user = this.userMapper.toEntity(registerDto);
        user.setHashPassword(this.passwordEncoder.encode(registerDto.getPassword()));
        user.setStatus(UserStatus.PROFILE_INCOMPLETE);
        user.setCreatedAt(LocalDateTime.now());
        UserEntity savedUser = this.userRepository.save(user);
        return savedUser.getId();

    }
    
    @Override
    public int chooseRole(int userId, UserRole role)
    {
        UserEntity user = this.userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("this user does not exist"));
        user.setRole(role);
        UserEntity savedUser = this.userRepository.save(user);
        return savedUser.getId();
    }

    @Transactional
    @Override
    public StudentEntity registerStudent(StudentRegisterDto registerDto)
    {
        UserEntity user = this.userRepository.findById(registerDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!user.getRole().equals(UserRole.STUDENT))
        {
            throw new InvalidUserDataException("User havent choosed the student role");
        }
        SchoolClassEntity schoolClass = this.schoolClassRepository.findById(registerDto.getSchoolClassId())
                .orElseThrow(() -> new SchoolClassNotFoundException("Class not found"));

        StudentEntity student = new StudentEntity();
        student.setUser(user);
        student.setSchoolClass(schoolClass);
        student.setNumberInClass(-1);
        

        user.setStatus(UserStatus.NOTVERIFIED);
        this.userRepository.save(user);

        return this.studentRepository.save(student);
    }

    @Transactional
    @Override
    public TeacherEntity registerTeacher(TeacherRegisterDto registerDto)
    {
        UserEntity user = this.userRepository.findById(registerDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!user.getRole().equals(UserRole.TEACHER))
        {
            throw new InvalidUserDataException("User havent choosed the teacher role");
        }
        
        TeacherEntity teacherEntity = new TeacherEntity();
        teacherEntity.setUser(user);
        teacherEntity = this.teacherRepository.save(teacherEntity);
        Set<TeacherSubjectClassEntity> teacherAssiments = new HashSet<>();
        for(TeacherSubjectRegisterDto teacherSubjectDto : registerDto.getAssignments())
        {
            SubjectEntity subject = this.subjectRepository.findById(teacherSubjectDto.getSubjectId())
            .orElseThrow(() -> new SubjectNotFoundException("this subject does not exist"));
            for(int classId : teacherSubjectDto.getClassIds())
            {
                TeacherSubjectClassEntity teacherSubjectClassEntity = new TeacherSubjectClassEntity();
                SchoolClassEntity schoolClass = this.schoolClassRepository.findById(classId)
                .orElseThrow(() -> new SchoolClassNotFoundException("this class does not exist"));
                teacherSubjectClassEntity.setSchoolClass(schoolClass);
                teacherSubjectClassEntity.setSubject(subject);
                teacherSubjectClassEntity.setTeacher(teacherEntity);
                if(this.teacherSubjectClassR.existsBySubjectIdAndSchoolClassId(subject.getId(), classId))
                {
                    throw new InvalidUserDataException("this subject is already tought to this class by another teacher");
                }
                teacherAssiments.add(teacherSubjectClassEntity);
                this.teacherSubjectClassR.save(teacherSubjectClassEntity);
            }
        }
        user.setStatus(UserStatus.NOTVERIFIED);
        teacherEntity.setUser(user);
        teacherEntity.setTeacherAssignments(teacherAssiments);
        return this.teacherRepository.save(teacherEntity);

    }


    @Transactional
    @Override
    public void validateRegisterToken(String email, String token)
    {
        UserEntity user = this.userRepository.findByEmail(email)
        .orElseThrow(()->new UserNotFoundException("user not found"));

        EmailVerificationEntity emailVerification = this.emailValidationRepo.findByUser(user)
        .orElseThrow(()->new VerificationTokenNotFoundException("no verification send for user"));
        if(this.isTokenExpired(emailVerification.getCreatedAt()))
        {
            throw new TokenExpiredException("Verification token has expired");
        }
        if(emailVerification.isVerified()) 
        {
            throw new VerificationAlreadyCompleteException("This account is already verified");
        }
        if(!emailVerification.getToken().equals(token))
        {
            throw new InvalidTokenException("wrong code");
        }
        emailVerification.setVerified(true);
        this.emailValidationRepo.save(emailVerification);
        user.setStatus(UserStatus.ACTIVE);
        if(user.getRole().equals(UserRole.STUDENT))
        {
            StudentEntity student = this.studentRepository.findByUser(user)
            .orElseThrow(() -> new UserNotFoundException("user not found"));
            int studentNumb = this.studentRepository.countActiveBeforeAlphabetically(student.getSchoolClass().getId(), user.getUsername()) + 1;
            student.setNumberInClass(studentNumb);
            this.studentRepository.save(student);
            this.studentRepository.incrementActiveNumbersAfter(student.getSchoolClass().getId(), student.getUser().getUsername());
        }
        this.userRepository.save(user);

    }
    
    @Transactional
    @Override
    public void sendVerificationEmail(String email)
    {
         
        UserEntity user = this.userRepository.findByEmail(email)
        .orElseThrow(()-> new UserNotFoundException("user does not exist"));
        EmailVerificationEntity verification = this.emailValidationRepo.findByUser(user)
            .orElse(new EmailVerificationEntity(user));

        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        verification.setToken(String.valueOf(code));
        verification.setCreatedAt(LocalDateTime.now());
        verification.setVerified(false);
        
        this.emailValidationRepo.save(verification);


        try
        {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(new InternetAddress("kaloian3141@gmail.com", "Dazkatraz"));
            helper.setTo(email);
            helper.setSubject("Verification Code - Dazkatraz");
            helper.setText("<h2>Code: " + verification.getToken() + "</h2>" +
                "<p> This code expires in 60 minutes </p>", true);
            this.javaMailSender.send(mimeMessage);
        }
        catch(Exception e)
        {
            throw new EmailSendException("Fail to send email to " + user.getEmail() + ":", e);
        }
    }

    @Override
    public boolean isTokenExpired(LocalDateTime createdAt) 
    {
        long minutesPassed = ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now());
        return minutesPassed > 60; 
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        return User
                .withUsername(user.getEmail())
                .password(user.getHashPassword())
                .roles(user.getRole().name())
                .build();
    }


    @Transactional()
    @Override
    public List<SchoolClassResponseDto> getAllClasses() {
        List<SchoolClassEntity> classes = this.schoolClassRepository.findAll();
        return this.schoolClassMapper.toDtoList(classes);
    }

    @Transactional()
    @Override
    public List<SubjectResponseDto> getAllSubjects() {
        List<SubjectEntity> subjects = this.subjectRepository.findAll();
        return this.subjectMapper.toDtoList(subjects);
    }

    @Transactional()
    @Override
    public List<SchoolClassResponseDto> getAvailableClassesForSubject(int subjectId) 
    {
        List<Integer> occupiedClassIds = this.teacherSubjectClassR.findOccupiedClassIdsBySubject(subjectId);
        
        List<SchoolClassEntity> availableClasses;
        
        if(occupiedClassIds.isEmpty()) 
        {
            availableClasses = this.schoolClassRepository.findAll();
        } 
        else 
        {
            availableClasses = this.schoolClassRepository.findAllByIdNotIn(occupiedClassIds);
        }
        
        return this.schoolClassMapper.toDtoList(availableClasses);
    }
}

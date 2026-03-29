package com.example.ElDnevniko.services.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ElDnevniko.domain.dtos.LoginRequestDto;
import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.dtos.UserRegisterDto;
import com.example.ElDnevniko.domain.entities.EmailVerificationEntity;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.domain.entities.UserStatus;
import com.example.ElDnevniko.exceptions.AccountNotActivatedException;
import com.example.ElDnevniko.exceptions.InvalidCredentialsException;
import com.example.ElDnevniko.exceptions.InvalidTokenException;
import com.example.ElDnevniko.exceptions.InvalidUserDataException;
import com.example.ElDnevniko.exceptions.TokenExpiredException;
import com.example.ElDnevniko.exceptions.UserAlreadyExistException;
import com.example.ElDnevniko.exceptions.UserNotFoundException;
import com.example.ElDnevniko.exceptions.VerificationAlreadyCompleteException;
import com.example.ElDnevniko.exceptions.VerificationTokenNotFoundException;
import com.example.ElDnevniko.mappers.StudentMapper;
import com.example.ElDnevniko.mappers.TeacherMapper;
import com.example.ElDnevniko.mappers.UserMapper;
import com.example.ElDnevniko.repositories.EmailVerificationRepository;
import com.example.ElDnevniko.repositories.StudentRepository;
import com.example.ElDnevniko.repositories.TeacherRepository;
import com.example.ElDnevniko.repositories.UserRepository;
import com.example.ElDnevniko.services.AuthService;

import jakarta.transaction.Transactional;
@Service
public class AuthServiceImpl implements AuthService
{
    
    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private UserMapper userMapper;
    private StudentMapper studentMapper;
    private TeacherMapper teacherMapper;
    private PasswordEncoder passwordEncoder;
    private EmailVerificationRepository emailValidationRepo;
    private JavaMailSender javaMailSender;
    
    public AuthServiceImpl(
        UserRepository userRepository,
        StudentRepository studentRepository,
        TeacherRepository teacherRepository,
        UserMapper userMapper,
        StudentMapper studentMapper,
        TeacherMapper teacherMapper,
        PasswordEncoder passwordEncoder,
        EmailVerificationRepository emailVR,
        JavaMailSender javaMS
    )
    {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.userMapper = userMapper;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailValidationRepo = emailVR;
        this.javaMailSender = javaMS;
    }

    @Transactional
    @Override
    public void registerUser(UserRegisterDto registerDto)
    {
        if(registerDto.getEmail().isEmpty())
        {
            throw new InvalidUserDataException("email must not be empty");
        }
        if(registerDto.getUsername().isEmpty())
        {
            throw new InvalidUserDataException("username must not be empty");
        }
        if(registerDto.getPassword().isEmpty())
        {
            throw new InvalidUserDataException("password must not be empty");
        }

        if(this.userRepository.existsByEmail(registerDto.getEmail()))
        {
            throw new UserAlreadyExistException("User with email " + registerDto.getEmail() + "already exists");
        }
        if(this.userRepository.existsByUsername(registerDto.getUsername()))
        {
            throw new UserAlreadyExistException("User with username " + registerDto.getUsername() + "already exists");
        }
        String hashPassword = this.passwordEncoder.encode(registerDto.getPassword());
        registerDto.setPassword(hashPassword);
        UserEntity user = this.userMapper.toEntity(registerDto);
        UserEntity savedUser = this.userRepository.save(user);
        switch(registerDto.getRole()) 
        {
            case STUDENT:
                registerStudent((StudentRegisterDto)registerDto, savedUser);
                break;
            case TEACHER:
                registerTeacher((TeacherRegisterDto)registerDto, savedUser);
                break;
            default:
                throw new InvalidUserDataException("This role does not exists");
        }    

    }
    
    @Override
    public void registerStudent(StudentRegisterDto registerDto, UserEntity user)
    {
        if(registerDto.getNumberInClass() <= 0)
        {
            throw new InvalidUserDataException("Number in class must be greater than 0");
        }
        StudentEntity student = this.studentMapper.toEntity(registerDto);
        student.setUser(user);
        this.studentRepository.save(student);
    }

    @Override
    public void registerTeacher(TeacherRegisterDto registerDto, UserEntity user)
    {
        TeacherEntity teacher = this.teacherMapper.toEntity(registerDto);
        teacher.setUser(user);
        this.teacherRepository.save(teacher);
    }

    @Transactional
    @Override
    public void login(LoginRequestDto loginDto)
    {
        if(loginDto.getEmail().isEmpty())
        {
            throw new InvalidUserDataException("email must not be empty");
        }
        if(loginDto.getPassword().isEmpty())
        {
            throw new InvalidUserDataException("password must not be empty");
        }
        if(!this.userRepository.existsByEmail(loginDto.getEmail()))
        {
            throw new InvalidCredentialsException("No user with " + loginDto.getEmail() + " email exists");
        }
        UserEntity user = this.userRepository.findByEmail(loginDto.getEmail()).get();
        if(!this.passwordEncoder.matches(loginDto.getPassword(), user.getHashPassword()))
        {
            throw new InvalidCredentialsException("Invalid password");
        }
        if(!user.getStatus().equals(UserStatus.ACTIVE))
        {
            throw new AccountNotActivatedException("this account is not activated");
        }
    }   
    
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

        verification.setToken(UUID.randomUUID().toString());
        verification.setCreatedAt(LocalDateTime.now());
        verification.setVerified(false);
        
        this.emailValidationRepo.save(verification);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification code for Dazkatraz");
        message.setText("Code: " + verification.getToken() + "\n\n This code expires in 60 minutes");
        try
        {
            this.javaMailSender.send(message);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Fail to send email to " + user.getEmail() + ":" + e.getMessage());
        }
    }

    @Override
    public boolean isTokenExpired(LocalDateTime createdAt) 
    {
        long minutesPassed = ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now());
        return minutesPassed > 60; 
    }

}

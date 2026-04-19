package com.example.ElDnevniko.serviceTests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.ElDnevniko.domain.dtos.LoginRequestDto;
import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.entities.EmailVerificationEntity;
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
import com.example.ElDnevniko.repositories.EmailVerificationRepository;
import com.example.ElDnevniko.repositories.UserRepository;
import com.example.ElDnevniko.services.AuthService;
import com.example.ElDnevniko.testUtils.TestDtoData;
@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthServiceTest {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private EmailVerificationRepository emailVerificationRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    public void registerStudentTest()
    {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        assertDoesNotThrow(() -> this.authService.registerUser(student));
        
    }
    
    @Test
    public void registerStudentWithAlreadyExistingUsernameOrEmail()
    {
        StudentRegisterDto student1 = TestDtoData.createStudentDto();
        this.authService.registerUser(student1);
        StudentRegisterDto student2 = TestDtoData.createStudentDto();
        student2.setEmail("newEmail");
        assertThrows(UserAlreadyExistException.class, 
                    () -> this.authService.registerUser(student2));
        StudentRegisterDto student3 = TestDtoData.createStudentDto();
        student3.setUsername("newUsername");
        assertThrows(UserAlreadyExistException.class,
                    () -> this.authService.registerUser(student3));

    }

    @Test
    public void registerStudentWithInvalidClassNumber()
    {
        StudentRegisterDto student1 = TestDtoData.createStudentDto();
        student1.setNumberInClass(0);
        assertThrows(InvalidUserDataException.class,
                    () -> this.authService.registerUser(student1));

    }
    
    @Test
    public void registerStudentWithInvalidUsernameOrEmailOrPassword()
    {
        StudentRegisterDto student1 = TestDtoData.createStudentDto();
        student1.setUsername("");
        assertThrows(InvalidUserDataException.class,
                    () -> this.authService.registerUser(student1));
        student1.setUsername("ValidUsername");
        student1.setEmail("");
        assertThrows(InvalidUserDataException.class,
                    () -> this.authService.registerUser(student1));
        student1.setEmail("ValidEmail");
        student1.setPassword("");
        assertThrows(InvalidUserDataException.class,
                    () -> this.authService.registerUser(student1));
        student1.setPassword("ValidPassword");
        assertDoesNotThrow(() -> this.authService.registerUser(student1));
    }
    
    @Test
    public void loginUserTest()
    {
        StudentRegisterDto student1 = TestDtoData.createStudentDto();
        this.authService.registerUser(student1);
        UserEntity user = this.userRepository.findByEmail(student1.getEmail()).get();
        user.setStatus(UserStatus.ACTIVE);
        this.userRepository.save(user);
        LoginRequestDto loginDto = TestDtoData.createTestLogin();
        
        assertDoesNotThrow(() -> this.authService.login(loginDto));
    }

    @Test
    public void loginUserWithInvalidEmail()
    {
        StudentRegisterDto student1 = TestDtoData.createStudentDto();
        this.authService.registerUser(student1);
        LoginRequestDto loginDto = TestDtoData.createTestLogin();
        loginDto.setEmail("invalidEmail");
        assertThrows(InvalidCredentialsException.class,
                    () -> this.authService.login(loginDto));
    }
    
    @Test
    public void loginUserWithInvalidPassword()
    {
        StudentRegisterDto student1 = TestDtoData.createStudentDto();
        this.authService.registerUser(student1);
        LoginRequestDto loginDto = TestDtoData.createTestLogin();
        loginDto.setPassword("wrongPassword");
        assertThrows(InvalidCredentialsException.class,
                    () -> this.authService.login(loginDto));
    }

    @Test
    public void loginUserWithNotActivatedAccount() {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        this.authService.registerUser(student);
        
        LoginRequestDto loginDto = TestDtoData.createTestLogin();
        
        assertThrows(AccountNotActivatedException.class, 
            () -> this.authService.login(loginDto));
    }

    @Test 
    public void validateRegisterTokenTest() 
    {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        this.authService.registerUser(student);
        this.authService.sendVerificationEmail(student.getEmail());
        UserEntity user = this.userRepository.findByEmail(student.getEmail()).get();
        EmailVerificationEntity emailV = this.emailVerificationRepository.findByUser(user).get();
        assertDoesNotThrow(() -> this.authService.validateRegisterToken(user.getEmail(), emailV.getToken()));
    }

    @Test
    public void validateRegisterWithWrongTokenTest() 
    {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        student.setEmail("newEmail@gmail.com");
        this.authService.registerUser(student);
        this.authService.sendVerificationEmail(student.getEmail());
        assertThrows(InvalidTokenException.class, 
            () -> this.authService.validateRegisterToken(student.getEmail(), "WRONG_TOKEN"));
    }

    @Test 
    public void validateRegisterAfterExpiredTime()
    {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        student.setEmail("newEmail2@gmail.com");
        this.authService.registerUser(student);
        this.authService.sendVerificationEmail(student.getEmail());
        UserEntity user = this.userRepository.findByEmail(student.getEmail()).get();
        EmailVerificationEntity emailV = this.emailVerificationRepository.findByUser(user).get();
        emailV.setCreatedAt(LocalDateTime.now().minusMinutes(70));
        this.emailVerificationRepository.save(emailV);
        assertThrows(TokenExpiredException.class,
            () -> this.authService.validateRegisterToken(user.getEmail(), emailV.getToken())
        );
    }

    @Test
    public void validateActiveUser() {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        this.authService.registerUser(student);
        this.authService.sendVerificationEmail(student.getEmail());
        
        UserEntity user = this.userRepository.findByEmail(student.getEmail()).get();
        EmailVerificationEntity emailV = this.emailVerificationRepository.findByUser(user).get();
        
        this.authService.validateRegisterToken(user.getEmail(), emailV.getToken());
        
        assertThrows(VerificationAlreadyCompleteException.class, 
            () -> this.authService.validateRegisterToken(user.getEmail(), emailV.getToken()));
    }

    @Test
    public void validateRegisterWhenNoTokenSended() {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        this.authService.registerUser(student);
        assertThrows(VerificationTokenNotFoundException.class, 
            () -> this.authService.validateRegisterToken(student.getEmail(), "token"));
    }
    
    @Test 
    public void createAndSendEmailVerificationTest()
    {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        student.setEmail("newEmail3@gmail.com");
        this.authService.registerUser(student);
        assertDoesNotThrow(() -> this.authService.sendVerificationEmail(student.getEmail()));
    }

    @Test
    public void createAndSEndEmailVerificationToWrongUser()
    {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        this.authService.registerUser(student);
        assertThrows(UserNotFoundException.class, 
            () -> this.authService.sendVerificationEmail("wrong_email"));
    }

    @Test
    public void resendEmailVerificationShouldReplaceOldToken() {
        StudentRegisterDto student = TestDtoData.createStudentDto();
        this.authService.registerUser(student);
        this.authService.sendVerificationEmail(student.getEmail());
        
        UserEntity user = this.userRepository.findByEmail(student.getEmail()).get();
        String firstToken = this.emailVerificationRepository.findByUser(user).get().getToken();

        this.authService.sendVerificationEmail(student.getEmail());
        
        EmailVerificationEntity secondTokenEntity = this.emailVerificationRepository.findByUser(user).get();
        
        assertNotEquals(firstToken, secondTokenEntity.getToken());
        assertEquals(1, this.emailVerificationRepository.count()); 
    }

}

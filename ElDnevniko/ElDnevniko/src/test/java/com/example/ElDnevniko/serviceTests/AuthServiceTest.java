package com.example.ElDnevniko.serviceTests;

import static org.junit.jupiter.api.Assertions.*;
 
import java.time.LocalDateTime;
import java.util.List;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
 
import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.domain.dtos.TeacherRegisterDto;
import com.example.ElDnevniko.domain.dtos.TeacherSubjectRegisterDto;
import com.example.ElDnevniko.domain.dtos.UserRegisterDto;
import com.example.ElDnevniko.domain.entities.EmailVerificationEntity;
import com.example.ElDnevniko.domain.entities.SchoolClassEntity;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.SubjectEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.domain.entities.UserEntity;
import com.example.ElDnevniko.domain.entities.UserRole;
import com.example.ElDnevniko.domain.entities.UserStatus;
import com.example.ElDnevniko.exceptions.InvalidTokenException;
import com.example.ElDnevniko.exceptions.InvalidUserDataException;
import com.example.ElDnevniko.exceptions.SchoolClassNotFoundException;
import com.example.ElDnevniko.exceptions.SubjectNotFoundException;
import com.example.ElDnevniko.exceptions.TokenExpiredException;
import com.example.ElDnevniko.exceptions.UserAlreadyExistException;
import com.example.ElDnevniko.exceptions.UserNotFoundException;
import com.example.ElDnevniko.exceptions.VerificationAlreadyCompleteException;
import com.example.ElDnevniko.exceptions.VerificationTokenNotFoundException;
import com.example.ElDnevniko.repositories.EmailVerificationRepository;
import com.example.ElDnevniko.repositories.SchoolClassRepository;
import com.example.ElDnevniko.repositories.StudentRepository;
import com.example.ElDnevniko.repositories.SubjectRepository;
import com.example.ElDnevniko.repositories.UserRepository;
import com.example.ElDnevniko.services.AuthService;
import com.example.ElDnevniko.testUtils.TestDtoData;
import com.example.ElDnevniko.testUtils.TestEntityData;
 
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
    private StudentRepository studentRepository;
 
    @Autowired
    private SchoolClassRepository schoolClassRepository;
 
    @Autowired
    private SubjectRepository subjectRepository;
 
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @MockitoBean private JavaMailSender mailSender;
    
    private UserRegisterDto validUserDto;
    private SchoolClassEntity testClass;
    private SubjectEntity testSubject;
 
    @BeforeEach
    public void setUp() 
    {
        validUserDto = UserRegisterDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("TestPassword123")
                .build();
 
        testClass = this.schoolClassRepository.save(TestEntityData.createTestSchoolClass());
        testSubject = this.subjectRepository.save(TestEntityData.createTestSubject());
    }
 
 
    @Test
    public void testRegisterUserSuccess() 
    {
        int userId = this.authService.registerUser(validUserDto);
 
        assertNotEquals(0, userId);
        assertTrue(this.userRepository.existsById(userId));
 
        UserEntity savedUser = this.userRepository.findById(userId).get();
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals(UserStatus.PROFILE_INCOMPLETE, savedUser.getStatus());
        assertEquals(UserRole.NOTPROVIDED, savedUser.getRole());
    }
 
    @Test
    public void testRegisterUserWithDuplicateEmail() 
    {
        UserEntity existingUser = UserEntity.builder()
                                .email("duplicate@example.com")
                                .username("existingUser")
                                .hashPassword("pass")
                                .status(UserStatus.ACTIVE).build(); 

        this.userRepository.save(existingUser);
        UserRegisterDto duplicateEmailDto = new UserRegisterDto();
        duplicateEmailDto.setEmail("duplicate@example.com");
        duplicateEmailDto.setUsername("newUser");
        duplicateEmailDto.setPassword("password123");
        assertThrows(UserAlreadyExistException.class,
                () -> this.authService.registerUser(duplicateEmailDto));
    }
 
    @Test
    public void testRegisterUserWithDuplicateUsername() 
    {
        UserEntity existingUser = UserEntity.builder()
                                .email("email@example.com")
                                .username("duplicatingUser")
                                .hashPassword("pass")
                                .status(UserStatus.ACTIVE).build(); 

        this.userRepository.save(existingUser);
        UserRegisterDto duplicateUsernameDto = new UserRegisterDto();
        duplicateUsernameDto.setEmail("newemail@example.com");
        duplicateUsernameDto.setUsername("duplicatingUser");
        duplicateUsernameDto.setPassword("password123");
        assertThrows(UserAlreadyExistException.class,
                () -> this.authService.registerUser(duplicateUsernameDto));
    }

 
    @Test
    public void testRegisterUserCanReplaceUnverifiedUser() 
    {
        this.authService.registerUser(validUserDto);
        UserEntity firstUser = this.userRepository.findByEmail("test@example.com").get();
        assertEquals(UserStatus.PROFILE_INCOMPLETE, firstUser.getStatus());
 
        int newUserId = this.authService.registerUser(validUserDto);
 
        assertNotEquals(firstUser.getId(), newUserId);
        assertEquals(1, this.userRepository.count());
    }
 
 
    @Test
    public void testChooseRoleStudent() 
    {
        int userId = this.authService.registerUser(validUserDto);
 
        int resultId = this.authService.chooseRole(userId, UserRole.STUDENT);
 
        assertEquals(userId, resultId);
        UserEntity user = this.userRepository.findById(userId).get();
        assertEquals(UserRole.STUDENT, user.getRole());
    }
 
    @Test
    public void testChooseRoleTeacher() 
    {
        int userId = this.authService.registerUser(validUserDto);
 
        int resultId = this.authService.chooseRole(userId, UserRole.TEACHER);
 
        assertEquals(userId, resultId);
        UserEntity user = this.userRepository.findById(userId).get();
        assertEquals(UserRole.TEACHER, user.getRole());
    }
 
    @Test
    public void testChooseRoleForNonexistentUser() 
    {
        assertThrows(UserNotFoundException.class,
                () -> this.authService.chooseRole(999, UserRole.STUDENT));
    }
 
 
    @Test
    public void testRegisterStudentSuccess() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.STUDENT);
 
        StudentRegisterDto studentDto = TestDtoData.createStudentProfileDto(userId, testClass.getId());
 
        StudentEntity result = this.authService.registerStudent(studentDto);
 
        assertNotNull(result);
        assertEquals(userId, result.getUser().getId());
        assertEquals(testClass.getId(), result.getSchoolClass().getId());
 
        UserEntity user = this.userRepository.findById(userId).get();
        assertEquals(UserStatus.NOTVERIFIED, user.getStatus());
    }
 
    @Test
    public void testRegisterStudentWithWrongRole() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.TEACHER);
 
        StudentRegisterDto studentDto = StudentRegisterDto.builder()
                .userId(userId)
                .schoolClassId(testClass.getId())
                .build();
 
        assertThrows(InvalidUserDataException.class,
                () -> this.authService.registerStudent(studentDto));
    }
 
    @Test
    public void testRegisterStudentWithInvalidClass() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.STUDENT);
 
        StudentRegisterDto studentDto = StudentRegisterDto.builder()
                .userId(userId)
                .schoolClassId(999)
                .build();
 
        assertThrows(SchoolClassNotFoundException.class,
                () -> this.authService.registerStudent(studentDto));
    }
 
    @Test
    public void testRegisterStudentWithNonexistentUser() 
    {
        StudentRegisterDto studentDto = StudentRegisterDto.builder()
                .userId(999)
                .schoolClassId(testClass.getId())
                .build();
 
        assertThrows(UserNotFoundException.class,
                () -> this.authService.registerStudent(studentDto));
    }
 
 
    @Test
    public void testRegisterTeacherSuccess() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.TEACHER);
 
        TeacherSubjectRegisterDto assignment = new TeacherSubjectRegisterDto(
                testSubject.getId(),
                List.of(testClass.getId())
        );
 
        TeacherRegisterDto teacherDto = TeacherRegisterDto.builder()
                .userId(userId)
                .assignments(List.of(assignment))
                .build();
 
        TeacherEntity result = this.authService.registerTeacher(teacherDto);
 
        assertNotNull(result);
        assertEquals(userId, result.getUser().getId());
        assertFalse(result.getTeacherAssignments().isEmpty());
 
        UserEntity user = this.userRepository.findById(userId).get();
        assertEquals(UserStatus.NOTVERIFIED, user.getStatus());
    }
 
    @Test
    public void testRegisterTeacherWithWrongRole() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.STUDENT);
 
        TeacherSubjectRegisterDto assignment = new TeacherSubjectRegisterDto(
                testSubject.getId(),
                List.of(testClass.getId())
        );
 
        TeacherRegisterDto teacherDto = TeacherRegisterDto.builder()
                .userId(userId)
                .assignments(List.of(assignment))
                .build();
 
        assertThrows(InvalidUserDataException.class,
                () -> this.authService.registerTeacher(teacherDto));
    }
 
    @Test
    public void testRegisterTeacherWithInvalidSubject() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.TEACHER);
 
        TeacherSubjectRegisterDto assignment = new TeacherSubjectRegisterDto(
                999,
                List.of(testClass.getId())
        );
 
        TeacherRegisterDto teacherDto = TeacherRegisterDto.builder()
                .userId(userId)
                .assignments(List.of(assignment))
                .build();
 
        assertThrows(SubjectNotFoundException.class,
                () -> this.authService.registerTeacher(teacherDto));
    }
 
    @Test
    public void testRegisterTeacherWithInvalidClass() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.TEACHER);
 
        TeacherSubjectRegisterDto assignment = new TeacherSubjectRegisterDto(
                testSubject.getId(),
                List.of(999)
        );
 
        TeacherRegisterDto teacherDto = TeacherRegisterDto.builder()
                .userId(userId)
                .assignments(List.of(assignment))
                .build();
 
        assertThrows(SchoolClassNotFoundException.class,
                () -> this.authService.registerTeacher(teacherDto));
    }
 
    @Test
    public void testRegisterTeacherWithMultipleAssignments() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.TEACHER);
 
        SubjectEntity subject2 = subjectRepository.save(SubjectEntity.builder()
                .subjectName("subject2")
                .subjectAssignments(new java.util.HashSet<>())
                .build());
 
        SchoolClassEntity class2 = schoolClassRepository.save(SchoolClassEntity.builder()
                .className("11B")
                .graduationYear(2027)
                .classAssignments(new java.util.HashSet<>())
                .students(new java.util.HashSet<>())
                .build());
 
        TeacherSubjectRegisterDto assignment1 = new TeacherSubjectRegisterDto(
                testSubject.getId(),
                List.of(testClass.getId(), class2.getId())
        );
 
        TeacherSubjectRegisterDto assignment2 = new TeacherSubjectRegisterDto(
                subject2.getId(),
                List.of(testClass.getId())
        );
 
        TeacherRegisterDto teacherDto = TeacherRegisterDto.builder()
                .userId(userId)
                .assignments(List.of(assignment1, assignment2))
                .build();
 
        TeacherEntity result = this.authService.registerTeacher(teacherDto);
 
        assertEquals(3, result.getTeacherAssignments().size());
    }
 
 
    @Test
    public void testSendVerificationEmailSuccess() 
    {
        int userId = this.authService.registerUser(validUserDto);
 
        assertDoesNotThrow(() -> this.authService.sendVerificationEmail(userId));
 
        EmailVerificationEntity verification = emailVerificationRepository
                .findByUser(this.userRepository.findById(userId).get())
                .get();
 
        assertNotNull(verification.getToken());
        assertFalse(verification.isVerified());
    }
 
    @Test
    public void testSendVerificationEmailToNonexistentUser() 
    {
        assertThrows(UserNotFoundException.class,
                () -> this.authService.sendVerificationEmail(999));
    }
 
    @Test
    public void testResendVerificationEmailReplacesToken() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.sendVerificationEmail(userId);
 
        UserEntity user = this.userRepository.findById(userId).get();
        String firstToken = emailVerificationRepository.findByUser(user).get().getToken();
 
        this.authService.sendVerificationEmail(userId);
 
        String secondToken = emailVerificationRepository.findByUser(user).get().getToken();
 
        assertNotEquals(firstToken, secondToken);
        assertEquals(1, emailVerificationRepository.count());
    }
 
    @Test
    public void testValidateRegisterTokenSuccess() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.sendVerificationEmail(userId);
 
        UserEntity user = this.userRepository.findById(userId).get();
        String token = emailVerificationRepository.findByUser(user).get().getToken();
 
        assertDoesNotThrow(() -> this.authService.validateRegisterToken(userId, token));
 
        user = this.userRepository.findById(userId).get();
        assertEquals(UserStatus.ACTIVE, user.getStatus());
 
        EmailVerificationEntity verification = emailVerificationRepository.findByUser(user).get();
        assertTrue(verification.isVerified());
    }
 
    @Test
    public void testValidateRegisterTokenWithWrongToken() 
    {
        this.authService.registerUser(validUserDto);
        this.authService.sendVerificationEmail(1);
 
        assertThrows(InvalidTokenException.class,
                () -> this.authService.validateRegisterToken(1, "WRONG_TOKEN"));
    }
 
    @Test
    public void testValidateRegisterTokenExpired() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.sendVerificationEmail(userId);
 
        UserEntity user = this.userRepository.findById(userId).get();
        EmailVerificationEntity verification = emailVerificationRepository.findByUser(user).get();
 
        verification.setCreatedAt(LocalDateTime.now().minusMinutes(61));
        emailVerificationRepository.save(verification);
 
        assertThrows(TokenExpiredException.class,
                () -> this.authService.validateRegisterToken(userId, verification.getToken()));
    }
 
    @Test
    public void testValidateRegisterTokenAlreadyVerified() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.sendVerificationEmail(userId);
 
        UserEntity user = this.userRepository.findById(userId).get();
        String token = emailVerificationRepository.findByUser(user).get().getToken();
        this.authService.validateRegisterToken(userId, token);
 
        assertThrows(VerificationAlreadyCompleteException.class,
                () -> this.authService.validateRegisterToken(userId, token));
    }
 
    @Test
    public void testValidateRegisterTokenNoTokenSent() 
    {
        this.authService.registerUser(validUserDto);
 
        assertThrows(VerificationTokenNotFoundException.class,
                () -> this.authService.validateRegisterToken(1, "anytoken"));
    }
 
    @Test
    public void testValidateRegisterTokenSetsStudentNumber() 
    {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.STUDENT);
 
        StudentRegisterDto studentDto = StudentRegisterDto.builder()
                .userId(userId)
                .schoolClassId(testClass.getId())
                .build();
 
        StudentEntity student = this.authService.registerStudent(studentDto);
        assertEquals(-1, student.getNumberInClass());
        this.authService.sendVerificationEmail(userId);
 
        UserEntity user = this.userRepository.findById(userId).get();
        String token = emailVerificationRepository.findByUser(user).get().getToken();
 
        this.authService.validateRegisterToken(userId, token);
 
        StudentEntity verifiedStudent = studentRepository.findByUser(user).get();
        assertNotEquals(-1, verifiedStudent.getNumberInClass());
        assertTrue(verifiedStudent.getNumberInClass() > 0);
    }
 
 
    @Test
    public void testLoadUserByUsernameSuccess() {
        this.authService.registerUser(validUserDto);
 
        UserDetails userDetails = this.authService.loadUserByUsername("test@example.com");
 
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("NOTPROVIDED")));
    }
 
    @Test
    public void testLoadUserByUsernameNotFound() {
        assertThrows(UsernameNotFoundException.class,
                () -> this.authService.loadUserByUsername("nonexistent@example.com"));
    }
 
    @Test
    public void testLoadUserByUsernameWithRole() {
        int userId = this.authService.registerUser(validUserDto);
        this.authService.chooseRole(userId, UserRole.STUDENT);
 
        UserDetails userDetails = this.authService.loadUserByUsername("test@example.com");
 
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("STUDENT")));
    }
 
 
    @Test
    public void testIsTokenExpired() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusMinutes(65);
        LocalDateTime recent = now.minusMinutes(30);
 
        assertTrue(this.authService.isTokenExpired(past));
        assertFalse(this.authService.isTokenExpired(recent));
    }
 
 
    @Test
    public void testGetAllClasses() {
        schoolClassRepository.save(TestEntityData.createTestSchoolClass());
        
        var classes = this.authService.getAllClasses();
 
        assertNotNull(classes);
        assertEquals(2, classes.size());
    }
 
    @Test
    public void testGetAllSubjects() 
    {
        subjectRepository.save(TestEntityData.createTestSubject());
        
        var subjects = this.authService.getAllSubjects();
 
        assertNotNull(subjects);
        assertEquals(2, subjects.size());
    }
 
    @Test
    public void testGetAvailableClassesForSubject() 
    {
        schoolClassRepository.save(SchoolClassEntity.builder()
                .className("11B")
                .graduationYear(2027)
                .classAssignments(new java.util.HashSet<>())
                .students(new java.util.HashSet<>())
                .build());
 
        var availableClasses = this.authService.getAvailableClassesForSubject(testSubject.getId());
 
        assertNotNull(availableClasses);
        assertEquals(2, availableClasses.size());
    }
}
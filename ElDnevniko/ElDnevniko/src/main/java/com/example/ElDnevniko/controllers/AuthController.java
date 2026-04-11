package com.example.ElDnevniko.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ElDnevniko.config.JwtUtil;
import com.example.ElDnevniko.domain.dtos.*;
import com.example.ElDnevniko.domain.entities.StudentEntity;
import com.example.ElDnevniko.domain.entities.TeacherEntity;
import com.example.ElDnevniko.exceptions.AccountNotActivatedException;
import com.example.ElDnevniko.exceptions.InvalidCredentialsException;
import com.example.ElDnevniko.services.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtils;
    
    @Autowired  
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Integer>> registerUser(@Valid @RequestBody UserRegisterDto registerDto)
    {
        int userId = this.authService.registerUser(registerDto);
        return ResponseEntity.ok(ApiResponse.success("base account created.", userId));
    }
    @PostMapping("/choose-role")
    public ResponseEntity<ApiResponse<Integer>> chooseRole(@Valid @RequestBody RoleChoiceRegisterDto dto)
    {
        int userId = this.authService.chooseRole(dto.getUserId(), dto.getUserRole());
        return ResponseEntity.ok(ApiResponse.success("user Choose Role successfully", userId));
    }
    
    @PostMapping("/register-student")
    public ResponseEntity<ApiResponse<String>> registerStudent(@Valid @RequestBody StudentRegisterDto studentDto)
    {
        StudentEntity saveStudent = this.authService.registerStudent(studentDto);
        this.authService.sendVerificationEmail(saveStudent.getUser().getEmail());
        return ResponseEntity.ok(ApiResponse.success("Student Account created successfuly. verify email", saveStudent.getUser().getEmail()));
    }

    @PostMapping("/register-teacher")
    public ResponseEntity<ApiResponse<String>> registerTeacher(@Valid @RequestBody TeacherRegisterDto teacherDto)
    {
        TeacherEntity saveTeacher = this.authService.registerTeacher(teacherDto);
        this.authService.sendVerificationEmail(saveTeacher.getUser().getEmail());
        return ResponseEntity.ok(ApiResponse.success("Teacher Account created successfuly. verify email", saveTeacher.getUser().getEmail()));
    }

    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<SchoolClassResponseDto>>> getAllClasses() {
        return ResponseEntity.ok(ApiResponse.success("Classes shown to user", 
                                                    this.authService.getAllClasses()));
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getAllSubjects() {
        return ResponseEntity.ok(ApiResponse.success("subject shown to user", 
                                                    this.authService.getAllSubjects()));
    }

    @GetMapping("/subjects/{subjectId}/available-classes")
    public ResponseEntity<ApiResponse<List<SchoolClassResponseDto>>> getAvailableClassesForSubject(@Valid @PathVariable int subjectId) 
    {
        return ResponseEntity.ok(ApiResponse.success("availeble subjects shown", 
                                                    this.authService.getAvailableClassesForSubject(subjectId)));
    }

    @PostMapping("/send-email-verification")
    public ResponseEntity<ApiResponse<String>> sendVerification(@Valid @RequestParam String email)
    {
        this.authService.sendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Verification code send successfully", email));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyUser(@Valid @RequestParam String email,
                                                          @Valid @RequestParam String token)
    {
        this.authService.validateRegisterToken(email, token);
        return ResponseEntity.ok(ApiResponse.success("Account validated.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginUser(@Valid @RequestBody LoginRequestDto loginDto)
    {
        try{

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success("Login successful", 
                                                    jwtUtils.generateToken(userDetails.getUsername())));
        }
        catch(BadCredentialsException e) 
        {   
            throw new InvalidCredentialsException("Invalid email or password");
        } 
        catch(DisabledException e) 
        {
            throw new AccountNotActivatedException("Account not verified. Please verify your email");
        }
    }
}

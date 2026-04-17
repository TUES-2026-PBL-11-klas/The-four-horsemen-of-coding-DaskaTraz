package com.example.ElDnevniko.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ElDnevniko.config.JwtUtil;
import com.example.ElDnevniko.domain.dtos.ApiResponse;
import com.example.ElDnevniko.domain.dtos.LoginRequestDto;
import com.example.ElDnevniko.domain.dtos.StudentRegisterDto;
import com.example.ElDnevniko.services.AuthService;

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
    public ResponseEntity<ApiResponse<String>> register(@RequestBody StudentRegisterDto RegisterDto)
    {
        this.authService.registerUser(RegisterDto);
        this.authService.sendVerificationEmail(RegisterDto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Successful registration. Verify your email.", null));
    }
    
    @PostMapping("/send-email-verification")
    public ResponseEntity<ApiResponse<String>> sendVerification(@RequestParam String email)
    {
        this.authService.sendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Verification code send successfully", null));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyUser(@RequestParam String email,
                                                          @RequestParam String token)
    {
        this.authService.validateRegisterToken(email, token);
        return ResponseEntity.ok(ApiResponse.success("Account validated.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginUser(@RequestBody LoginRequestDto loginDto)
    {
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
}

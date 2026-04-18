package com.example.ElDnevniko.exceptions;

import java.security.SignatureException;

import javax.security.sasl.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.ElDnevniko.domain.dtos.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<String>> handleUserExists(UserAlreadyExistException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ApiResponse<String>> handleNotActivated(AccountNotActivatedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleTokenExpired(TokenExpiredException exception) {
        return ResponseEntity.status(HttpStatus.GONE).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorized(InvalidCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(VerificationAlreadyCompleteException.class)
    public ResponseEntity<ApiResponse<String>> handleAlreadyVerified(VerificationAlreadyCompleteException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(exception.getMessage()));
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidToken(InvalidTokenException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(VerificationTokenNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleTokenNotFound(VerificationTokenNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralError(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + exception.getMessage()));
    }

    @ExceptionHandler(SchoolClassNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleClassNotFound(SchoolClassNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleSubjectNotFound(SubjectNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthentication(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Authentication failed: " + e.getMessage()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<String>> handleExpiredToken(ExpiredJwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("JWT token has expired"));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<String>> handleMalformedToken(MalformedJwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid JWT token format"));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse<String>> handleSignatureException(SignatureException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("JWT signature validation failed"));
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiResponse<String>> handleUnsupportedToken(UnsupportedJwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("JWT token is not supported"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(
            MethodArgumentNotValidException exception) 
    {    
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorMessage));
    }
    @ExceptionHandler(GradeNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleGradeNotFound(GradeNotFoundException e)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(e.getMessage()));
    }
    @ExceptionHandler(TeacherAccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleTeacherAccessDenied(TeacherAccessDeniedException e)
    {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(e.getMessage()));
    }
    @ExceptionHandler(SubjectNotPresentInClassException.class)
    public ResponseEntity<ApiResponse<String>> handleSubjectNotPresentInClass(SubjectNotPresentInClassException e)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getMessage()));
    }
}
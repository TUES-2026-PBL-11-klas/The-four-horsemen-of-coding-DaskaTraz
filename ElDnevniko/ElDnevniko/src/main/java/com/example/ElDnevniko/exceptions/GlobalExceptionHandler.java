package com.example.ElDnevniko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.ElDnevniko.domain.dtos.ApiResponse;

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

    
}
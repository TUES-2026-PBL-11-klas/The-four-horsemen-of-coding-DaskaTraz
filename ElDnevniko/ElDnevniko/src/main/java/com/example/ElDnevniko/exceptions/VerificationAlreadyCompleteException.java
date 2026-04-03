package com.example.ElDnevniko.exceptions;

public class VerificationAlreadyCompleteException extends RuntimeException{
    public VerificationAlreadyCompleteException(String message)
    {
        super(message);
    }
}
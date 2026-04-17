package com.example.ElDnevniko.exceptions;

public class VerificationTokenNotFoundException extends RuntimeException{
    public VerificationTokenNotFoundException(String message)
    {
        super(message);
    }
}

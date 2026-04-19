package com.example.ElDnevniko.exceptions;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message)
    {
        super(message);
    }
}

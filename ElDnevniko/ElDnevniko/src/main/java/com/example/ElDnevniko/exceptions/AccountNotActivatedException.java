package com.example.ElDnevniko.exceptions;

public class AccountNotActivatedException extends RuntimeException{
    public AccountNotActivatedException(String message)
    {
        super(message);
    }
}

package com.example.ElDnevniko.exceptions;

public class SchoolClassNotFoundException extends RuntimeException {
    public SchoolClassNotFoundException(String message) {
        super(message);
    }
}
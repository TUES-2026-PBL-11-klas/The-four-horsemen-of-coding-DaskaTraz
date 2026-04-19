package com.example.ElDnevniko.exceptions;

public class SubjectNotPresentInClassException extends RuntimeException {
    public SubjectNotPresentInClassException(String message) {
        super(message);
    }
}
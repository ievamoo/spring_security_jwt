package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
} 
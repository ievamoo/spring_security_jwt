package com.example.demo.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String entity, String value) {
        super(entity + " already exists: " + value);
    }
}

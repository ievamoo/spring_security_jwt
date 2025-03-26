package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application that provides consistent error responses.
 * Handles various types of exceptions and converts them into appropriate HTTP responses.
 * 
 * Handles the following exceptions:
 * - UsernameNotFoundException: Returns 404 when a user is not found
 * - BadCredentialsException: Returns 401 for invalid login credentials
 * - AccessDeniedException: Returns 403 for unauthorized access or 404 for user not found cases
 * - RegistrationException: Returns 409 for registration conflicts (e.g., duplicate username)
 * - ResourceNotFoundException: Returns 404 when a requested resource is not found
 * - ResourceAlreadyExistsException: Returns 409 when trying to create a resource that already exists
 * - MethodArgumentNotValidException: Returns 400 with validation errors for invalid request data
 * - Exception: Returns 500 for any unhandled exceptions
 *
 * All responses follow a consistent format:
 * {
 *   "timestamp": "ISO-8601 timestamp",
 *   "message": "Error message",
 *   "status": HTTP status code,
 *   "error": "Error type"
 * }
 * 
 * For validation errors, an additional "errors" field is included with field-specific messages.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where a user is not found by username.
     * Returns HTTP 404 with a "User Not Found" message.
     *
     * @param ex The UsernameNotFoundException that was thrown
     * @return ResponseEntity with 404 status and error details
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "User Not Found"));
    }

    /**
     * Handles invalid login credentials.
     * Returns HTTP 401 with an "Authentication Failed" message.
     *
     * @param ex The BadCredentialsException that was thrown
     * @return ResponseEntity with 401 status and error details
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED, "Authentication Failed"));
    }

    /**
     * Handles access denied exceptions.
     * Returns HTTP 403 for general access denied cases.
     * Returns HTTP 404 if the access denied is due to a user not being found.
     *
     * @param ex The AccessDeniedException that was thrown
     * @return ResponseEntity with either 403 or 404 status and error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("User not found with username")) {
            String username = ex.getMessage().replace("User not found with username: ", "");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("User not found with username: " + username, HttpStatus.NOT_FOUND, "User Not Found"));
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse("Access denied", HttpStatus.FORBIDDEN, "Forbidden"));
    }

    /**
     * Handles registration-related exceptions (e.g., duplicate username).
     * Returns HTTP 409 with a "Registration Failed" message.
     *
     * @param ex The RegistrationException that was thrown
     * @return ResponseEntity with 409 status and error details
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Map<String, Object>> handleRegistrationException(RegistrationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, "Registration Failed"));
    }

    /**
     * Handles cases where a requested resource is not found.
     * Returns HTTP 404 with a "Resource Not Found" message.
     *
     * @param ex The ResourceNotFoundException that was thrown
     * @return ResponseEntity with 404 status and error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "Resource Not Found"));
    }

    /**
     * Handles cases where trying to create a resource that already exists.
     * Returns HTTP 409 with a "Resource Already Exists" message.
     *
     * @param ex The ResourceAlreadyExistsException that was thrown
     * @return ResponseEntity with 409 status and error details
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, "Resource Already Exists"));
    }

    /**
     * Handles validation errors from @Valid annotations.
     * Returns HTTP 400 with detailed field-specific validation errors.
     *
     * @param ex The MethodArgumentNotValidException that was thrown
     * @return ResponseEntity with 400 status and detailed validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));

        Map<String, Object> response = createErrorResponse(
                "Validation failed", 
                HttpStatus.BAD_REQUEST, 
                "Validation Error"
        );
        response.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles any unhandled exceptions.
     * Returns HTTP 500 with an "Internal Server Error" message.
     *
     * @param ex The Exception that was thrown
     * @return ResponseEntity with 500 status and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
    }

    /**
     * Creates a standardized error response map.
     *
     * @param message The error message
     * @param status The HTTP status
     * @param error The error type
     * @return Map containing the error response details
     */
    private Map<String, Object> createErrorResponse(String message, HttpStatus status, String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("message", message);
        response.put("status", status.value());
        response.put("error", error);
        return response;
    }
}

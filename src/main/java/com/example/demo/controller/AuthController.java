package com.example.demo.controller;

import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling authentication-related endpoints.
 * This controller provides endpoints for:
 * 1. User authentication and login
 * 2. New user registration
 * 3. JWT token generation for both login and registration
 *
 * The controller is configured to accept requests from the Angular frontend
 * running on http://localhost:4200 and provides stateless authentication
 * using JWT tokens.
 *
 * Base path: /api
 * Available endpoints:
 * - POST /login - User authentication
 * - POST /register - New user registration
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and generates a JWT token upon successful authentication.
     * This endpoint validates the provided credentials against the database and,
     * if valid, generates a JWT token containing the user's details and roles.
     *
     * @param authRequest DTO containing username and password for authentication
     * @return ResponseEntity containing:
     *         - 200 OK with JWT token on successful authentication
     *         - 400 Bad Request with error message on invalid credentials
     *         - 500 Internal Server Error on unexpected errors
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> createAuthenticationToken(@RequestBody AuthRequestDto authRequest) {
        String jwt = authService.authenticateAndGenerateToken(
                authRequest.getUsername(), authRequest.getPassword());
    

        return ResponseEntity.ok(new AuthResponseDto(jwt));
    }
    
    /**
     * Registers a new user and generates a JWT token for immediate authentication.
     * This endpoint:
     * 1. Validates the registration data (using @Valid)
     * 2. Creates a new user account
     * 3. Generates a JWT token for immediate login
     *
     * The registration request is validated for:
     * - Required fields (firstName, lastName, email, username, password)
     * - Valid email format
     * - Unique username and email
     *
     * @param request DTO containing user registration details
     * @return ResponseEntity containing:
     *         - 200 OK with JWT token on successful registration
     *         - 400 Bad Request with validation errors
     *         - 500 Internal Server Error on unexpected errors
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto request) {
        String jwt = authService.registerAndGenerateToken(request);
        return ResponseEntity.ok(new AuthResponseDto(jwt));
    }
}




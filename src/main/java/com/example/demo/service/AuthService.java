package com.example.demo.service;


import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class handling authentication and registration operations.
 * Provides functionality for:
 * - User authentication and JWT token generation
 * - New user registration with automatic token generation
 * - Password encryption
 * 
 * This service integrates with Spring Security for authentication
 * and uses JWT for generating secure tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Authenticates a user and generates a JWT token.
     * This method:
     * 1. Validates the credentials using Spring Security
     * 2. Loads the user details
     * 3. Generates a JWT token
     *
     * @param username the username for authentication
     * @param password the password for authentication
     * @return JWT token string if authentication is successful
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    public String authenticateAndGenerateToken(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }

    /**
     * Registers a new user and generates a JWT token for immediate authentication.
     * This method:
     * 1. Creates a new user with encrypted password
     * 2. Assigns default USER role
     * 3. Saves the user to the database
     * 4. Generates a JWT token
     *
     * @param request DTO containing registration information (username, password, etc.)
     * @return JWT token string for the newly registered user
     * @throws RuntimeException if username already exists
     */
    public String registerAndGenerateToken(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .roles(List.of(Role.USER))
                .build();

        userRepository.save(user);
        return generateToken(user.getUsername());
    }

    /**
     * Helper method to generate a JWT token for a given username.
     * Loads user details and uses JwtUtil to create the token.
     *
     * @param username the username to generate token for
     * @return JWT token string
     */
    private String generateToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }
}

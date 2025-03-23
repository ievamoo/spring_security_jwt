package com.example.demo.utils;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling authentication-related endpoints.
 * This controller provides endpoints for:
 * 1. User authentication
 * 2. JWT token generation
 * 
 * The controller is configured to accept requests from the Angular frontend
 * running on http://localhost:4200.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Authenticates a user and generates a JWT token upon successful authentication.
     * This endpoint:
     * 1. Validates the user credentials
     * 2. Generates a JWT token containing user details and roles
     * 3. Returns the token in the response
     *
     * @param authRequest Contains username and password for authentication
     * @return ResponseEntity containing:
     *         - JWT token on successful authentication
     *         - Error message on authentication failure
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            // First authenticate the user
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // If authentication is successful, load user details and generate token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        } catch (Exception e) {
            e.printStackTrace(); // Add this for debugging
            return ResponseEntity.internalServerError().body("Error during authentication: " + e.getMessage());
        }
    }
}




package com.example.demo.controller;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling user-related operations.
 * Provides endpoints for managing user profiles and account operations.
 * All endpoints in this controller require authentication.
 * Operations are performed on the authenticated user's own account,
 * using the authentication context to identify the user.
 *
 * Base path: /api/user
 * Security: Requires valid JWT token in Authorization header
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves the profile information of the currently authenticated user.
     * Uses the authentication context to identify the user and fetch their details.
     *
     * @param authentication Spring Security authentication object containing user details
     * @return ResponseEntity containing:
     *         - 200 OK with UserDto containing user information
     *         - 404 Not Found if user doesn't exist
     */
    @GetMapping
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        var currentUser = userService.getUserByUsername(username);
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Deletes the account of the currently authenticated user.
     * Uses the authentication context to identify the user to delete.
     * After successful deletion, the user's token will become invalid.
     *
     * @param authentication Spring Security authentication object containing user details
     * @return ResponseEntity:
     *         - 204 No Content on successful deletion
     *         - 404 Not Found if user doesn't exist
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the profile information of the currently authenticated user.
     * Only allows updating of non-sensitive information (firstName, lastName, email).
     * Username and password cannot be updated through this endpoint.
     *
     * @param authentication Spring Security authentication object containing user details
     * @param updatedUserDto DTO containing the updated user information
     * @return ResponseEntity containing:
     *         - 200 OK with updated UserDto
     *         - 400 Bad Request if validation fails
     *         - 404 Not Found if user doesn't exist
     */
    @PutMapping
    public ResponseEntity<UserDto> updateCurrentUser(Authentication authentication, @RequestBody UserDto updatedUserDto) {
        var username = authentication.getName();
        var updatedUser = userService.updateCurrentUser(username, updatedUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Retrieves all users in the system.
     * This endpoint is restricted to administrators only.
     *
     * @return ResponseEntity containing:
     *         - 200 OK with List<UserDto> containing all users
     *         - 403 Forbidden if the user is not an administrator
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<List<UserDto>> addUser(@RequestBody RegisterRequestDto newUser) {
        var user = userService.addUser(newUser);
        return ResponseEntity.ok(userService.getAllUsers());
    }

}

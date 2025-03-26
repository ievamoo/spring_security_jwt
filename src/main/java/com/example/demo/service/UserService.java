package com.example.demo.service;

import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.dto.UserDto;
import com.example.demo.exception.RegistrationException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class handling user-related business logic.
 * Provides functionality for user management operations including:
 * - User retrieval
 * - User deletion
 * - User profile updates
 * 
 * This service acts as an intermediary between the controllers and the repository,
 * handling data transformation between DTOs and entities, and implementing
 * business logic for user operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves a user by their username and converts it to a DTO.
     * 
     * @param username the username to search for
     * @return UserDto containing the user's information
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    public UserDto getUserByUsername(String username) {
        var matchingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return userMapper.modelToDto(matchingUser);
    }

    /**
     * Deletes a user account by username.
     * Throws an exception if the user doesn't exist to ensure the operation was successful.
     * 
     * @param username the username of the user to delete
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    public void deleteUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found:" + username));
        userRepository.delete(user);
    }

    /**
     * Updates the profile information of an existing user.
     * Only updates firstName, lastName, and email fields.
     * All other fields (id, username, password, roles) remain unchanged.
     * 
     * @param username the username of the user to update
     * @param updatedUserDto DTO containing the updated user information
     * @return UserDto containing the updated user information
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    public UserDto updateCurrentUser(String username, UserDto updatedUserDto) {
        var matchingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found:" + username));
        userMapper.updateUserProfile(updatedUserDto, matchingUser);
        var savedUser = userRepository.save(matchingUser);
        return userMapper.modelToDto(savedUser);
    }

    public List<UserDto> getAllUsers() {
        var users = userRepository.findAll();
        return users.stream()
                .map(userMapper::modelToDto)
                .toList();
    }

    public List<UserDto> addUser(RegisterRequestDto newUser) {
        validateEmailAndUsername(newUser);
        var user = User.builder()
                .username(newUser.getUsername())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .email(newUser.getEmail())
                .roles(List.of(Role.USER))
                .build();
        userRepository.save(user);
        return userRepository.findAll().stream()
                .map(userMapper::modelToDto)
                .toList();
    }

    private void validateEmailAndUsername(RegisterRequestDto newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new RegistrationException("Username already exists");
        }
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new ResourceAlreadyExistsException("Email", newUser.getEmail());
        }
    }
}


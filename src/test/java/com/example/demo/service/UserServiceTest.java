package com.example.demo.service;

import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.dto.UserDto;
import com.example.demo.exception.RegistrationException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .roles(List.of(Role.USER))
                .password("encodedPass")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }


    @Test
    void getUserByUsername_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userMapper.modelToDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserByUsername("john");

        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void getUserByUsername_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.getUserByUsername("ghost"));
    }

    @Test
    void deleteUserByUsername_ShouldDelete_WhenUserExists() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        userService.deleteUserByUsername("john");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserByUsername_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.deleteUserByUsername("ghost"));
    }


    @Test
    void updateCurrentUser_ShouldUpdateAndReturnDto_WhenUserExists() {
        UserDto updatedDto = UserDto.builder()
                .email("new@example.com")
                .firstName("Johnny")
                .lastName("Doestar")
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .username("john")
                .email("new@example.com")
                .firstName("Johnny")
                .lastName("Doestar")
                .password("unchangedPassword")
                .roles(List.of(Role.USER))
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserProfile(updatedDto, user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.modelToDto(updatedUser)).thenReturn(updatedDto);

        UserDto result = userService.updateCurrentUser("john", updatedDto);

        assertEquals("new@example.com", result.getEmail());
        assertEquals("Johnny", result.getFirstName());
        assertEquals("Doestar", result.getLastName());
        verify(userRepository).save(any(User.class));
    }


    @Test
    void updateCurrentUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.updateCurrentUser("missing", userDto));
    }


    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.modelToDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }


    @Test
    void addUser_ShouldAddAndReturnAllUsers_WhenValid() {
        RegisterRequestDto newUser = RegisterRequestDto.builder()
                .username("newuser")
                .password("password123")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .build();

        User newEntity = User.builder()
                .username("newuser")
                .password("encodedPass")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .roles(List.of(Role.USER))
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(newEntity);
        when(userRepository.findAll()).thenReturn(List.of(newEntity));
        when(userMapper.modelToDto(newEntity)).thenReturn(userDto);

        List<UserDto> result = userService.addUser(newUser);

        assertEquals(1, result.size());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_ShouldThrow_WhenUsernameExists() {
        RegisterRequestDto newUser = RegisterRequestDto.builder()
                .username("john")
                .email("another@example.com")
                .build();

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(RegistrationException.class, () ->
                userService.addUser(newUser));
    }

    @Test
    void addUser_ShouldThrow_WhenEmailExists() {
        RegisterRequestDto newUser = RegisterRequestDto.builder()
                .username("newuser")
                .email("john@example.com")
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
                userService.addUser(newUser));
    }
}

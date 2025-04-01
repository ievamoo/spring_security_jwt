package com.example.demo.service;

import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.exception.RegistrationException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private CustomUserDetailsService userDetailsService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void authenticateAndGenerateToken_ShouldReturnToken_WhenCredentialsAreValid() {
        String username = "testuser";
        String password = "password";
        String token = "mockJwtToken";

        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(password)
                .authorities("ROLE_USER")
                .build();

        when(userRepository.existsByUsername(username)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(mockUserDetails)).thenReturn(token);

        String result = authService.authenticateAndGenerateToken(username, password);

        assertEquals(token, result);
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Test
    void authenticateAndGenerateToken_ShouldThrow_WhenUserNotFound() {
        when(userRepository.existsByUsername("nouser")).thenReturn(false);
        assertThrows(Exception.class, () ->
                authService.authenticateAndGenerateToken("nouser", "password"));
    }

    @Test
    void authenticateAndGenerateToken_ShouldThrowBadCredentials_WhenAuthFails() {
        String username = "testuser";
        String password = "wrong";

        when(userRepository.existsByUsername(username)).thenReturn(true);
        doThrow(new BadCredentialsException("Invalid username or password"))
                .when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () ->
                authService.authenticateAndGenerateToken(username, password));
    }


    @Test
    void registerAndGenerateToken_ShouldRegisterAndReturnToken_WhenValid() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("newuser");
        request.setPassword("securepass");
        request.setEmail("new@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        String encodedPass = "encodedPass";
        String token = "generatedJwtToken";

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPass);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(request.getUsername())
                .password(encodedPass)
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername(request.getUsername())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        String result = authService.registerAndGenerateToken(request);

        assertEquals(token, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerAndGenerateToken_ShouldThrow_WhenUsernameExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("existinguser");
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(RegistrationException.class, () -> authService.registerAndGenerateToken(request));
    }

    @Test
    void registerAndGenerateToken_ShouldThrow_WhenEmailExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("newuser");
        request.setEmail("taken@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(RegistrationException.class, () -> authService.registerAndGenerateToken(request));
    }


    @Test
    void generateToken_ShouldReturnToken() {
        String username = "testuser";
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("pass")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwtToken");

        String token = authService.generateToken(username);

        assertEquals("jwtToken", token);
    }
}
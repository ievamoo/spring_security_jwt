package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.security.JwtRequestFilter;
import com.example.demo.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security configuration class that provides comprehensive security setup for the application.
 * This configuration includes:
 * - JWT-based authentication
 * - Role-based authorization
 * - CORS configuration for Angular frontend
 * - Stateless session management
 * - Password encryption
 * - User details service integration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Configures the security filter chain with comprehensive security settings.
     * Configuration includes:
     * - CORS configuration for Angular frontend
     * - CSRF disabled for JWT-based authentication
     * - Role-based access control (ADMIN and USER roles)
     * - Stateless session management
     * - JWT authentication filter
     * - Frame options for H2 console access
     *
     * @param http HttpSecurity object to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/login", "/api/register", "/error", "/h2-console/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/carPart/**").hasAnyAuthority(Role.ADMIN.name(), Role.USER.name())
                    .requestMatchers(HttpMethod.POST, "/api/carPart/**").hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, "/api/carPart/**").hasAuthority(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/carPart/**").hasAuthority(Role.ADMIN.name())
                    .requestMatchers("/api/suppliers/**").hasAuthority(Role.ADMIN.name())
                    .requestMatchers("/api/user/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
            );

        return http.build();
    }

    /**
     * Configures the authentication provider with custom UserDetailsService and password encoder.
     * This provider handles the authentication process using database-stored credentials.
     *
     * @return Configured DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Creates the authentication manager bean used for authenticating users.
     *
     * @param config AuthenticationConfiguration object
     * @return Configured AuthenticationManager
     * @throws Exception if authentication manager configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures CORS settings for the Angular frontend.
     * Allows requests from the frontend application and specific HTTP methods.
     *
     * @return Configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Creates the password encoder bean for encrypting passwords.
     * Uses BCrypt for secure password hashing.
     *
     * @return Configured PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
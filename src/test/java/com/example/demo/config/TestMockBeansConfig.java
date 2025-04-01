package com.example.demo.config;

import com.example.demo.security.JwtRequestFilter;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.CustomUserDetailsService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestMockBeansConfig {

    @Bean
    @Primary
    public CustomUserDetailsService customUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }

    @Bean
    @Primary
    public JwtUtil jwtUtil() {
        return Mockito.mock(JwtUtil.class);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(CustomUserDetailsService customUserDetailsService,
                                             JwtUtil jwtUtil) {
        return new JwtRequestFilter(customUserDetailsService, jwtUtil);
    }
}

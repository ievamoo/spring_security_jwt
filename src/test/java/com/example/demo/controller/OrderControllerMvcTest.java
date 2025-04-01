package com.example.demo.controller;

import com.example.demo.security.JwtRequestFilter;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({OrderControllerMvcTest.MockedBeansConfig.class, OrderControllerMvcTest.MethodSecurityEnabled.class})
class OrderControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("Should return 403 Forbidden when user is not admin")
    @WithMockUser(authorities = "USER") // USER ≠ ADMIN
    void getAllOrders_shouldForbid_whenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/orders/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 200 OK when user has ADMIN authority")
    @WithMockUser(authorities = "ADMIN")
    void getAllOrders_shouldReturnOk_whenAdmin() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of());
        mockMvc.perform(get("/api/orders/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class MockedBeansConfig {

        @Bean
        public JwtRequestFilter jwtRequestFilter(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
            return new JwtRequestFilter(customUserDetailsService, jwtUtil);
        }

        @Bean
        public CustomUserDetailsService customUserDetailsService() {
            return Mockito.mock(CustomUserDetailsService.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }

    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class MethodSecurityEnabled {
    }
}

package com.example.demo.controller;

import com.example.demo.config.TestMockBeansConfig;
import com.example.demo.config.TestSecurityConfig;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({TestSecurityConfig.class, TestMockBeansConfig.class})
class UserControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/user";

    @Test
    @DisplayName("GET /api/user - Authenticated user gets profile")
    @WithMockUser(username = "john")
    void getCurrentUser_WithValidUser_ShouldReturnUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.com")
                .build();

        when(userService.getUserByUsername(anyString())).thenReturn(userDto);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@doe.com"));

        verify(userService).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("DELETE /api/user - Authenticated user deletes own account")
    @WithMockUser(username = "john")
    void deleteCurrentUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL))
                .andExpect(status().isNoContent());

        verify(userService).deleteUserByUsername("john");
    }

    @Test
    @DisplayName("PUT /api/user - Authenticated user updates profile")
    @WithMockUser
    void updateCurrentUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();

        when(userService.updateCurrentUser(anyString(), any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService).updateCurrentUser(anyString(), any(UserDto.class));
    }

    @Test
    @DisplayName("GET /api/user/all - Admin can get all users")
    @WithMockUser(authorities  = "ADMIN")
    void getAllUsers_WithAdminAccess_ShouldReturnUsersList() throws Exception {
        List<UserDto> users = Arrays.asList(
                UserDto.builder().id(1L).firstName("John").lastName("Doe").email("john@example.com").build(),
                UserDto.builder().id(2L).firstName("Jane").lastName("Smith").email("jane@example.com").build()
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/user/all - Non-admin gets forbidden")
    @WithMockUser(authorities  = "USER")
    void getAllUsers_WithoutAdminAccess_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get(BASE_URL + "/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/user - Admin adds new user")
    @WithMockUser(authorities  = "ADMIN")
    void addUser_WithValidData_ShouldReturnUsersList() throws Exception {
        RegisterRequestDto newUser = RegisterRequestDto.builder()
                .username("newuser")
                .firstName("New")
                .lastName("User")
                .email("new@example.com")
                .password("password")
                .build();

        List<UserDto> users = Arrays.asList(
                UserDto.builder().id(1L).firstName("John").lastName("Doe").email("john@example.com").build(),
                UserDto.builder().id(2L).firstName("New").lastName("User").email("new@example.com").build()
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].firstName").value("New"));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("POST /api/user - Non-admin gets forbidden when adding user")
    @WithMockUser(authorities  = "USER")
    void addUser_WithoutAdminAccess_ShouldReturnForbidden() throws Exception {
        RegisterRequestDto newUser = RegisterRequestDto.builder()
                .username("newuser")
                .firstName("New")
                .lastName("User")
                .email("new@example.com")
                .password("password")
                .build();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isForbidden());
    }

}

package com.clinchain.backend.controller;

import com.clinchain.backend.dto.LoginRequest;
import com.clinchain.backend.dto.LoginResponse;
import com.clinchain.backend.dto.UserDto;
import com.clinchain.backend.exception.UnauthorizedException;
import com.clinchain.backend.service.AuthService;
import com.clinchain.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("grossiste", "password");

        loginResponse = LoginResponse.builder()
                .token("jwtToken123")
                .user(UserDto.builder()
                        .id("user-1")
                        .username("grossiste")
                        .role("GROSSISTE")
                        .build())
                .build();
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken123"))
                .andExpect(jsonPath("$.user.username").value("grossiste"))
                .andExpect(jsonPath("$.user.role").value("GROSSISTE"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest("grossiste", "wrongpassword");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new UnauthorizedException("Invalid username or password"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        // Arrange
        LoginRequest notFoundRequest = new LoginRequest("unknownUser", "password");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("User not found: unknownUser"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notFoundRequest)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testLogin_EmptyCredentials() throws Exception {
        // Arrange
        LoginRequest emptyRequest = new LoginRequest("", "");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "grossiste", roles = "GROSSISTE")
    void testGetCurrentUser_Success() throws Exception {
        // Arrange
        UserDto userDto = UserDto.builder()
                .id("user-1")
                .username("grossiste")
                .role("GROSSISTE")
                .build();

        when(userService.findByUsername("grossiste")).thenReturn(
                com.clinchain.backend.model.User.builder()
                        .id("user-1")
                        .username("grossiste")
                        .role(com.clinchain.backend.model.UserRole.GROSSISTE)
                        .build()
        );

        when(userService.toDto(any())).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(get("/auth/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("grossiste"))
                .andExpect(jsonPath("$.role").value("GROSSISTE"));
    }

    @Test
    void testGetCurrentUser_Unauthorized() throws Exception {
        // Act & Assert - No authentication
        // Spring Security returns 403 Forbidden by default for unauthenticated requests
        mockMvc.perform(get("/auth/me")
                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "grossiste", roles = "GROSSISTE")
    void testLogout_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testLogout_Unauthorized() throws Exception {
        // Act & Assert - No authentication
        // Spring Security returns 403 Forbidden by default for unauthenticated requests
        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());
    }

    @Test
    void testLogin_WithDifferentRole() throws Exception {
        // Arrange
        LoginRequest pharmacistRequest = new LoginRequest("pharmacist", "password");
        LoginResponse pharmacistResponse = LoginResponse.builder()
                .token("pharmacistToken")
                .user(UserDto.builder()
                        .id("user-3")
                        .username("pharmacist")
                        .role("PHARMACISTE")
                        .build())
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(pharmacistResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pharmacistRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role").value("PHARMACISTE"));
    }
}

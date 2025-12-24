package com.clinchain.backend.service;

import com.clinchain.backend.config.JwtTokenProvider;
import com.clinchain.backend.dto.LoginRequest;
import com.clinchain.backend.dto.LoginResponse;
import com.clinchain.backend.dto.UserDto;
import com.clinchain.backend.exception.UnauthorizedException;
import com.clinchain.backend.model.User;
import com.clinchain.backend.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("1")
                .username("grossiste")
                .password("encodedPassword123")
                .role(UserRole.GROSSISTE)
                .build();

        loginRequest = new LoginRequest("grossiste", "password123");
    }

    @Test
    void testLogin_Success() {
        // Arrange
        when(userService.findByUsername("grossiste")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(tokenProvider.generateToken("grossiste", "GROSSISTE")).thenReturn("jwtToken123");
        when(userService.toDto(testUser)).thenReturn(
                UserDto.builder()
                        .id("1")
                        .username("grossiste")
                        .role("GROSSISTE")
                        .build()
        );

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken123", response.getToken());
        assertEquals("grossiste", response.getUser().getUsername());
        assertEquals("GROSSISTE", response.getUser().getRole());

        // Verify interactions
        verify(userService).findByUsername("grossiste");
        verify(passwordEncoder).matches("password123", "encodedPassword123");
        verify(tokenProvider).generateToken("grossiste", "GROSSISTE");
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest("grossiste", "wrongPassword");
        when(userService.findByUsername("grossiste")).thenReturn(testUser);
        when(passwordEncoder.matches(anyString(), eq("encodedPassword123"))).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            authService.login(invalidRequest);
        });

        verify(userService).findByUsername("grossiste");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword123");
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userService.findByUsername("unknownUser")).thenThrow(
                new RuntimeException("User not found: unknownUser")
        );

        // Act & Assert
        LoginRequest invalidRequest = new LoginRequest("unknownUser", "password123");
        assertThrows(RuntimeException.class, () -> {
            authService.login(invalidRequest);
        });

        verify(userService).findByUsername("unknownUser");
    }

    @Test
    void testLogin_WithDifferentRole() {
        // Arrange
        User pharmacist = User.builder()
                .id("2")
                .username("pharmacist")
                .password("encodedPassword456")
                .role(UserRole.PHARMACIEN)
                .build();

        LoginRequest pharmacistRequest = new LoginRequest("pharmacist", "password456");

        when(userService.findByUsername("pharmacist")).thenReturn(pharmacist);
        when(passwordEncoder.matches("password456", "encodedPassword456")).thenReturn(true);
        when(tokenProvider.generateToken("pharmacist", "PHARMACIEN")).thenReturn("jwtTokenPharmacist");
        when(userService.toDto(pharmacist)).thenReturn(
                UserDto.builder()
                        .id("2")
                        .username("pharmacist")
                        .role("PHARMACIEN")
                        .build()
        );

        // Act
        LoginResponse response = authService.login(pharmacistRequest);

        // Assert
        assertNotNull(response);
        assertEquals("pharmacist", response.getUser().getUsername());
        assertEquals("PHARMACIEN", response.getUser().getRole());
    }
}

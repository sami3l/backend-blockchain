package com.clinchain.backend.service;

import com.clinchain.backend.dto.UserDto;
import com.clinchain.backend.exception.ResourceNotFoundException;
import com.clinchain.backend.model.User;
import com.clinchain.backend.model.UserRole;
import com.clinchain.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .username("grossiste")
                .password("encodedPassword")
                .role(UserRole.GROSSISTE)
                .build();
    }

    @Test
    void testFindByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("grossiste")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findByUsername("grossiste");

        // Assert
        assertNotNull(result);
        assertEquals("user-123", result.getId());
        assertEquals("grossiste", result.getUsername());
        assertEquals(UserRole.GROSSISTE, result.getRole());

        // Verify
        verify(userRepository).findByUsername("grossiste");
    }

    @Test
    void testFindByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.findByUsername("unknownUser");
        });

        verify(userRepository).findByUsername("unknownUser");
    }

    @Test
    void testFindByUsername_WithPharmacist() {
        // Arrange
        User pharmacist = User.builder()
                .id("user-456")
                .username("pharmacist")
                .password("encodedPassword")
                .role(UserRole.PHARMACIEN)
                .build();

        when(userRepository.findByUsername("pharmacist")).thenReturn(Optional.of(pharmacist));

        // Act
        User result = userService.findByUsername("pharmacist");

        // Assert
        assertNotNull(result);
        assertEquals("pharmacist", result.getUsername());
        assertEquals(UserRole.PHARMACIEN, result.getRole());
    }

    @Test
    void testToDto_Conversion() {
        // Act
        UserDto dto = userService.toDto(testUser);

        // Assert
        assertNotNull(dto);
        assertEquals("user-123", dto.getId());
        assertEquals("grossiste", dto.getUsername());
        assertEquals("GROSSISTE", dto.getRole());
    }

    @Test
    void testToDto_WithDifferentRoles() {
        // Arrange
        User hospital = User.builder()
                .id("user-789")
                .username("hospital")
                .password("encodedPassword")
                .role(UserRole.HOPITALE)
                .build();

        // Act
        UserDto dto = userService.toDto(hospital);

        // Assert
        assertNotNull(dto);
        assertEquals("hospital", dto.getUsername());
        assertEquals("HOPITALE", dto.getRole());
    }

    @Test
    void testFindByUsername_ErrorMessage() {
        // Arrange
        String unknownUsername = "nonExistentUser";
        when(userRepository.findByUsername(unknownUsername)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findByUsername(unknownUsername)
        );

        assertTrue(exception.getMessage().contains(unknownUsername));
    }
}

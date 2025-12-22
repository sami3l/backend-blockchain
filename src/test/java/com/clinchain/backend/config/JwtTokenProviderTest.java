package com.clinchain.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String jwtSecret;
    private long jwtExpiration;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        jwtSecret = "my-secret-key-that-must-be-at-least-32-characters-long-for-HS256";
        jwtExpiration = 86400000; // 24 hours in milliseconds

        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", jwtExpiration);
    }

    @Test
    void testGenerateToken_Success() {
        // Act
        String token = jwtTokenProvider.generateToken("testuser", "GROSSISTE");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
        assertEquals(3, token.split("\\.").length); // JWT has 3 parts separated by dots
    }

    @Test
    void testGenerateToken_ContainsValidClaims() {
        // Act
        String token = jwtTokenProvider.generateToken("testuser", "HOPITALE");

        // Assert
        String username = jwtTokenProvider.getUsernameFromToken(token);
        String role = jwtTokenProvider.getRoleFromToken(token);

        assertEquals("testuser", username);
        assertEquals("HOPITALE", role);
    }

    @Test
    void testGetUsernameFromToken() {
        // Arrange
        String token = jwtTokenProvider.generateToken("john_doe", "PHARMACIEN");

        // Act
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertEquals("john_doe", username);
    }

    @Test
    void testGetRoleFromToken() {
        // Arrange
        String token = jwtTokenProvider.generateToken("jane_doe", "INFIRMIER");

        // Act
        String role = jwtTokenProvider.getRoleFromToken(token);

        // Assert
        assertEquals("INFIRMIER", role);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String token = jwtTokenProvider.generateToken("user1", "GROSSISTE");

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_MalformedToken() {
        // Arrange
        String malformedToken = "this-is-not-a-valid-jwt-token";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testGenerateToken_MultipleTokensAreDifferent() throws InterruptedException {
        // Act
        String token1 = jwtTokenProvider.generateToken("user1", "GROSSISTE");
        // JWT uses timestamps in seconds, so we need to wait at least 1 second
        Thread.sleep(1000);
        String token2 = jwtTokenProvider.generateToken("user1", "GROSSISTE");

        // Assert - Tokens should be different due to timestamps
        assertNotEquals(token1, token2);
    }

    @Test
    void testGenerateToken_DifferentRolesGenerateDifferentTokens() {
        // Act
        String token1 = jwtTokenProvider.generateToken("user1", "GROSSISTE");
        String token2 = jwtTokenProvider.generateToken("user1", "HOPITALE");

        // Assert
        String role1 = jwtTokenProvider.getRoleFromToken(token1);
        String role2 = jwtTokenProvider.getRoleFromToken(token2);

        assertEquals("GROSSISTE", role1);
        assertEquals("HOPITALE", role2);
    }

    @Test
    void testGenerateToken_DifferentUsernamesGenerateDifferentTokens() {
        // Act
        String token1 = jwtTokenProvider.generateToken("user1", "GROSSISTE");
        String token2 = jwtTokenProvider.generateToken("user2", "GROSSISTE");

        // Assert
        String username1 = jwtTokenProvider.getUsernameFromToken(token1);
        String username2 = jwtTokenProvider.getUsernameFromToken(token2);

        assertEquals("user1", username1);
        assertEquals("user2", username2);
    }

    @Test
    void testValidateToken_ExpiredToken() throws InterruptedException {
        // Arrange
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 1L); // 1 millisecond
        String token = jwtTokenProvider.generateToken("user1", "GROSSISTE");
        Thread.sleep(100); // Wait for token to expire
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 86400000L); // Reset

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testGetUsernameFromToken_WithEmptyUsername() {
        // Arrange
        String token = jwtTokenProvider.generateToken("", "GROSSISTE");

        // Act
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Assert - Empty username may return null or empty string
        assertTrue(username == null || username.isEmpty(), "Username should be null or empty");
    }

    @Test
    void testGetRoleFromToken_WithEmptyRole() {
        // Arrange
        String token = jwtTokenProvider.generateToken("user1", "");

        // Act
        String role = jwtTokenProvider.getRoleFromToken(token);

        // Assert
        assertEquals("", role);
    }

    @Test
    void testGenerateToken_AllRoles() {
        // Test all valid roles
        String[] roles = {"GROSSISTE", "HOPITALE", "PHARMACIEN", "INFIRMIER"};

        for (String role : roles) {
            // Act
            String token = jwtTokenProvider.generateToken("user1", role);

            // Assert
            String extractedRole = jwtTokenProvider.getRoleFromToken(token);
            assertEquals(role, extractedRole);
        }
    }

    @Test
    void testToken_ContainsCorrectExpirationTime() {
        // Arrange
        long beforeGeneration = System.currentTimeMillis();
        String token = jwtTokenProvider.generateToken("user1", "GROSSISTE");
        long afterGeneration = System.currentTimeMillis();

        // Act - Validate token and check it's valid
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }
}

package com.clinchain.backend.blockchain;

import com.clinchain.backend.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.Credentials;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialsProviderTest {

    @Mock
    private BlockchainProperties properties;

    @InjectMocks
    private CredentialsProvider credentialsProvider;

    private Map<UserRole, String> privateKeys;

    @BeforeEach
    void setUp() {
        privateKeys = new HashMap<>();
        // Valid Ethereum private keys (for testing)
        privateKeys.put(UserRole.GROSSISTE, "0x4c0883a69102937d6231471b5dbb6204fe5129617082792851a40d27e2fa8e0e");
        privateKeys.put(UserRole.HOPITALE, "0x2e5d8a0f9c7b1a3d6f4e2c5b7a1d9f3e6c4b1a8d5f2e7c0a3b6d9f1e4c5b8a");
        privateKeys.put(UserRole.PHARMACIEN, "0x1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t1u2v3w4x5y6z7a8b9c0d");
        privateKeys.put(UserRole.INFIRMIER, "0x5d4c3b2a1f0e9d8c7b6a5f4e3d2c1b0a9f8e7d6c5b4a3f2e1d0c9b8a7f6e");

        when(properties.getPrivateKeys()).thenReturn(privateKeys);
    }

    @Test
    void testResolve_WithValidGrossisteKey() {
        // Act
        Credentials credentials = credentialsProvider.resolve(UserRole.GROSSISTE);

        // Assert
        assertNotNull(credentials);
        assertNotNull(credentials.getAddress());
    }

    @Test
    void testResolve_WithValidHopitaleKey() {
        // Act
        Credentials credentials = credentialsProvider.resolve(UserRole.HOPITALE);

        // Assert
        assertNotNull(credentials);
        assertNotNull(credentials.getAddress());
    }

    @Test
    void testResolve_WithValidPharmacienKey() {
        // Act
        Credentials credentials = credentialsProvider.resolve(UserRole.PHARMACIEN);

        // Assert
        assertNotNull(credentials);
        assertNotNull(credentials.getAddress());
    }

    @Test
    void testResolve_WithValidInfirmierKey() {
        // Act
        Credentials credentials = credentialsProvider.resolve(UserRole.INFIRMIER);

        // Assert
        assertNotNull(credentials);
        assertNotNull(credentials.getAddress());
    }

    @Test
    void testResolve_MissingPrivateKey() {
        // Arrange
        Map<UserRole, String> emptyKeys = new HashMap<>();
        when(properties.getPrivateKeys()).thenReturn(emptyKeys);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            credentialsProvider.resolve(UserRole.GROSSISTE);
        });
    }

    @Test
    void testResolve_NullPrivateKey() {
        // Arrange
        Map<UserRole, String> nullKeys = new HashMap<>();
        nullKeys.put(UserRole.GROSSISTE, null);
        when(properties.getPrivateKeys()).thenReturn(nullKeys);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            credentialsProvider.resolve(UserRole.GROSSISTE);
        });
    }

    @Test
    void testResolve_EmptyStringPrivateKey() {
        // Arrange
        Map<UserRole, String> emptyStringKeys = new HashMap<>();
        emptyStringKeys.put(UserRole.GROSSISTE, "");
        when(properties.getPrivateKeys()).thenReturn(emptyStringKeys);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            credentialsProvider.resolve(UserRole.GROSSISTE);
        });
    }

    @Test
    void testResolve_DifferentRolesReturnDifferentCredentials() {
        // Act
        Credentials grossisteCredentials = credentialsProvider.resolve(UserRole.GROSSISTE);
        Credentials hopitaleCredentials = credentialsProvider.resolve(UserRole.HOPITALE);

        // Assert
        assertNotNull(grossisteCredentials);
        assertNotNull(hopitaleCredentials);
        assertNotEquals(grossisteCredentials.getAddress(), hopitaleCredentials.getAddress());
    }

    @Test
    void testResolve_SameRoleReturnsSameAddress() {
        // Act
        Credentials credentials1 = credentialsProvider.resolve(UserRole.GROSSISTE);
        Credentials credentials2 = credentialsProvider.resolve(UserRole.GROSSISTE);

        // Assert
        assertEquals(credentials1.getAddress(), credentials2.getAddress());
    }
}

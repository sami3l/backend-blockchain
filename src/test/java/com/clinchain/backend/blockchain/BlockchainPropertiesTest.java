package com.clinchain.backend.blockchain;

import com.clinchain.backend.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainPropertiesTest {

    private BlockchainProperties properties;

    @BeforeEach
    void setUp() {
        properties = new BlockchainProperties();
    }

    @Test
    void testSetAndGetRpcUrl() {
        // Arrange
        String rpcUrl = "http://localhost:8545";

        // Act
        properties.setRpcUrl(rpcUrl);

        // Assert
        assertEquals(rpcUrl, properties.getRpcUrl());
    }

    @Test
    void testSetAndGetContractAddress() {
        // Arrange
        String contractAddress = "0x1234567890123456789012345678901234567890";

        // Act
        properties.setContractAddress(contractAddress);

        // Assert
        assertEquals(contractAddress, properties.getContractAddress());
    }

    @Test
    void testSetAndGetChainId() {
        // Arrange
        Long chainId = 1337L;

        // Act
        properties.setChainId(chainId);

        // Assert
        assertEquals(chainId, properties.getChainId());
    }

    @Test
    void testSetAndGetGasPrice() {
        // Arrange
        BigInteger gasPrice = BigInteger.valueOf(20000000000L);

        // Act
        properties.setGasPrice(gasPrice);

        // Assert
        assertEquals(gasPrice, properties.getGasPrice());
    }

    @Test
    void testSetAndGetGasLimit() {
        // Arrange
        BigInteger gasLimit = BigInteger.valueOf(6721975L);

        // Act
        properties.setGasLimit(gasLimit);

        // Assert
        assertEquals(gasLimit, properties.getGasLimit());
    }

    @Test
    void testSetAndGetPrivateKeys() {
        // Arrange
        Map<UserRole, String> privateKeys = new EnumMap<>(UserRole.class);
        privateKeys.put(UserRole.GROSSISTE, "0x4c0883a69102937d6231471b5dbb6204fe5129617082792851a40d27e2fa8e0e");
        privateKeys.put(UserRole.HOPITALE, "0x2e5d8a0f9c7b1a3d6f4e2c5b7a1d9f3e6c4b1a8d5f2e7c0a3b6d9f1e4c5b8a");

        // Act
        properties.setPrivateKeys(privateKeys);

        // Assert
        assertNotNull(properties.getPrivateKeys());
        assertEquals(2, properties.getPrivateKeys().size());
        assertEquals("0x4c0883a69102937d6231471b5dbb6204fe5129617082792851a40d27e2fa8e0e",
                properties.getPrivateKeys().get(UserRole.GROSSISTE));
        assertEquals("0x2e5d8a0f9c7b1a3d6f4e2c5b7a1d9f3e6c4b1a8d5f2e7c0a3b6d9f1e4c5b8a",
                properties.getPrivateKeys().get(UserRole.HOPITALE));
    }

    @Test
    void testPrivateKeysInitializedAsEnumMap() {
        // Assert - Default initialization should be an EnumMap
        assertNotNull(properties.getPrivateKeys());
        assertTrue(properties.getPrivateKeys() instanceof EnumMap);
    }

    @Test
    void testAddPrivateKeyForEachRole() {
        // Arrange
        Map<UserRole, String> privateKeys = properties.getPrivateKeys();

        // Act
        privateKeys.put(UserRole.GROSSISTE, "grossiste-key");
        privateKeys.put(UserRole.HOPITALE, "hopital-key");
        privateKeys.put(UserRole.PHARMACIEN, "pharmacien-key");
        privateKeys.put(UserRole.INFIRMIER, "infirmier-key");

        // Assert
        assertEquals(4, properties.getPrivateKeys().size());
        assertEquals("grossiste-key", properties.getPrivateKeys().get(UserRole.GROSSISTE));
        assertEquals("hopital-key", properties.getPrivateKeys().get(UserRole.HOPITALE));
        assertEquals("pharmacien-key", properties.getPrivateKeys().get(UserRole.PHARMACIEN));
        assertEquals("infirmier-key", properties.getPrivateKeys().get(UserRole.INFIRMIER));
    }

    @Test
    void testSetAllProperties() {
        // Arrange & Act
        properties.setRpcUrl("http://localhost:8545");
        properties.setContractAddress("0xABC123");
        properties.setChainId(1337L);
        properties.setGasPrice(BigInteger.valueOf(20000000000L));
        properties.setGasLimit(BigInteger.valueOf(6721975L));

        Map<UserRole, String> keys = new EnumMap<>(UserRole.class);
        keys.put(UserRole.GROSSISTE, "key1");
        properties.setPrivateKeys(keys);

        // Assert
        assertEquals("http://localhost:8545", properties.getRpcUrl());
        assertEquals("0xABC123", properties.getContractAddress());
        assertEquals(1337L, properties.getChainId());
        assertEquals(BigInteger.valueOf(20000000000L), properties.getGasPrice());
        assertEquals(BigInteger.valueOf(6721975L), properties.getGasLimit());
        assertEquals(1, properties.getPrivateKeys().size());
    }
}

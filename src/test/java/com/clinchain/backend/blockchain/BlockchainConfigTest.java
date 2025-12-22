package com.clinchain.backend.blockchain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlockchainConfigTest {

    @Mock
    private BlockchainProperties properties;

    private BlockchainConfig blockchainConfig;

    @BeforeEach
    void setUp() {
        blockchainConfig = new BlockchainConfig(properties);
    }

    @Test
    void testWeb3jBean_WithValidRpcUrl() {
        // Arrange
        when(properties.getRpcUrl()).thenReturn("http://localhost:8545");

        // Act
        Web3j web3j = blockchainConfig.web3j();

        // Assert
        assertNotNull(web3j);
    }

    @Test
    void testWeb3jBean_WithDifferentRpcUrl() {
        // Arrange
        when(properties.getRpcUrl()).thenReturn("http://ganache:7545");

        // Act
        Web3j web3j = blockchainConfig.web3j();

        // Assert
        assertNotNull(web3j);
    }

    @Test
    void testContractGasProvider_WithCustomValues() {
        // Arrange
        BigInteger customGasPrice = BigInteger.valueOf(30000000000L);
        BigInteger customGasLimit = BigInteger.valueOf(8000000L);

        when(properties.getGasPrice()).thenReturn(customGasPrice);
        when(properties.getGasLimit()).thenReturn(customGasLimit);

        // Act
        ContractGasProvider gasProvider = blockchainConfig.contractGasProvider();

        // Assert
        assertNotNull(gasProvider);
        assertEquals(customGasPrice, gasProvider.getGasPrice());
        assertEquals(customGasLimit, gasProvider.getGasLimit());
    }

    @Test
    void testContractGasProvider_WithNullGasPrice_UsesDefault() {
        // Arrange
        BigInteger customGasLimit = BigInteger.valueOf(8000000L);

        when(properties.getGasPrice()).thenReturn(null);
        when(properties.getGasLimit()).thenReturn(customGasLimit);

        // Act
        ContractGasProvider gasProvider = blockchainConfig.contractGasProvider();

        // Assert
        assertNotNull(gasProvider);
        assertEquals(DefaultGasProvider.GAS_PRICE, gasProvider.getGasPrice());
        assertEquals(customGasLimit, gasProvider.getGasLimit());
    }

    @Test
    void testContractGasProvider_WithNullGasLimit_UsesDefault() {
        // Arrange
        BigInteger customGasPrice = BigInteger.valueOf(30000000000L);

        when(properties.getGasPrice()).thenReturn(customGasPrice);
        when(properties.getGasLimit()).thenReturn(null);

        // Act
        ContractGasProvider gasProvider = blockchainConfig.contractGasProvider();

        // Assert
        assertNotNull(gasProvider);
        assertEquals(customGasPrice, gasProvider.getGasPrice());
        assertEquals(DefaultGasProvider.GAS_LIMIT, gasProvider.getGasLimit());
    }

    @Test
    void testContractGasProvider_WithBothNull_UsesDefaults() {
        // Arrange
        when(properties.getGasPrice()).thenReturn(null);
        when(properties.getGasLimit()).thenReturn(null);

        // Act
        ContractGasProvider gasProvider = blockchainConfig.contractGasProvider();

        // Assert
        assertNotNull(gasProvider);
        assertEquals(DefaultGasProvider.GAS_PRICE, gasProvider.getGasPrice());
        assertEquals(DefaultGasProvider.GAS_LIMIT, gasProvider.getGasLimit());
    }

    @Test
    void testContractGasProvider_WithZeroValues() {
        // Arrange
        BigInteger zeroGasPrice = BigInteger.ZERO;
        BigInteger zeroGasLimit = BigInteger.ZERO;

        when(properties.getGasPrice()).thenReturn(zeroGasPrice);
        when(properties.getGasLimit()).thenReturn(zeroGasLimit);

        // Act
        ContractGasProvider gasProvider = blockchainConfig.contractGasProvider();

        // Assert
        assertNotNull(gasProvider);
        assertEquals(zeroGasPrice, gasProvider.getGasPrice());
        assertEquals(zeroGasLimit, gasProvider.getGasLimit());
    }

    @Test
    void testContractGasProvider_WithVeryLargeValues() {
        // Arrange
        BigInteger largeGasPrice = new BigInteger("100000000000000");
        BigInteger largeGasLimit = new BigInteger("10000000000");

        when(properties.getGasPrice()).thenReturn(largeGasPrice);
        when(properties.getGasLimit()).thenReturn(largeGasLimit);

        // Act
        ContractGasProvider gasProvider = blockchainConfig.contractGasProvider();

        // Assert
        assertNotNull(gasProvider);
        assertEquals(largeGasPrice, gasProvider.getGasPrice());
        assertEquals(largeGasLimit, gasProvider.getGasLimit());
    }
}

package com.clinchain.backend.blockchain;

import com.clinchain.backend.dto.BlockchainLotDto;
import com.clinchain.backend.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tuples.generated.Tuple5;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyChainGatewayTest {

    @Mock
    private Web3j web3j;

    @Mock
    private ContractGasProvider gasProvider;

    @Mock
    private BlockchainProperties properties;

    @Mock
    private CredentialsProvider credentialsProvider;

    @Mock
    private SupplyChain supplyChain;

    @Mock
    private TransactionReceipt transactionReceipt;

    @InjectMocks
    private SupplyChainGateway supplyChainGateway;

    private String testLotId;

    @BeforeEach
    void setUp() {
        testLotId = "550e8400-e29b-41d4-a716-446655440000";
        when(properties.getContractAddress()).thenReturn("0x123abc");
        when(properties.getChainId()).thenReturn(5777L);
        when(transactionReceipt.getTransactionHash()).thenReturn("0xabc123");
    }

    @Test
    void testCreerLot_Success() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(UserRole.GROSSISTE)).thenReturn(credentials);
        when(supplyChain.creerLot(any(BigInteger.class), anyString()).send())
                .thenReturn(transactionReceipt);

        // Act
        TransactionReceipt result = supplyChainGateway.creerLot(testLotId, "Paracetamol", UserRole.GROSSISTE);

        // Assert
        assertNotNull(result);
        assertEquals("0xabc123", result.getTransactionHash());
    }

    @Test
    void testValiderReceptionHopital_Success() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(UserRole.HOPITALE)).thenReturn(credentials);
        when(supplyChain.validerReceptionHopital(any(BigInteger.class)).send())
                .thenReturn(transactionReceipt);

        // Act
        TransactionReceipt result = supplyChainGateway.validerReceptionHopital(testLotId, UserRole.HOPITALE);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testMettreEnPharmacie_Success() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(UserRole.PHARMACIEN)).thenReturn(credentials);
        when(supplyChain.mettreEnPharmacie(any(BigInteger.class)).send())
                .thenReturn(transactionReceipt);

        // Act
        TransactionReceipt result = supplyChainGateway.mettreEnPharmacie(testLotId, UserRole.PHARMACIEN);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testAdministerPatient_Success() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(UserRole.INFIRMIER)).thenReturn(credentials);
        when(supplyChain.administrerPatient(any(BigInteger.class)).send())
                .thenReturn(transactionReceipt);

        // Act
        TransactionReceipt result = supplyChainGateway.administrerPatient(testLotId, UserRole.INFIRMIER);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testCreerLot_BlockchainError() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(UserRole.GROSSISTE)).thenReturn(credentials);
        when(supplyChain.creerLot(any(BigInteger.class), anyString()).send())
                .thenThrow(new RuntimeException("Connection timeout"));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            supplyChainGateway.creerLot(testLotId, "Ibuprofen", UserRole.GROSSISTE);
        });
    }

    @Test
    void testGetLotFromBlockchain_Success() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(any(UserRole.class))).thenReturn(credentials);

        // Mock the Tuple5 response
        Tuple5<BigInteger, String, BigInteger, String, BigInteger> mockTuple = mock(Tuple5.class);
        when(mockTuple.getValue1()).thenReturn(BigInteger.ONE);
        when(mockTuple.getValue2()).thenReturn("Paracetamol");
        when(mockTuple.getValue3()).thenReturn(BigInteger.ONE); // Status: VALIDE_PAR_HOPITAL
        when(mockTuple.getValue4()).thenReturn("0xhopital");
        when(mockTuple.getValue5()).thenReturn(BigInteger.valueOf(1703252000000L));

        when(supplyChain.lots(any(BigInteger.class)).send()).thenReturn(mockTuple);

        // Act
        BlockchainLotDto result = supplyChainGateway.getLotFromBlockchain(testLotId);

        // Assert
        assertNotNull(result);
        assertEquals(testLotId, result.getLotId());
        assertEquals("Paracetamol", result.getName());
        assertEquals(1, result.getBlockchainStatus());
        assertEquals("VALIDE_PAR_HOPITAL", result.getStatusName());
    }

    @Test
    void testGetLotFromBlockchain_StatusMapping() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(any(UserRole.class))).thenReturn(credentials);

        Tuple5<BigInteger, String, BigInteger, String, BigInteger> mockTuple = mock(Tuple5.class);
        when(mockTuple.getValue1()).thenReturn(BigInteger.ONE);
        when(mockTuple.getValue2()).thenReturn("Ibuprofen");
        when(mockTuple.getValue3()).thenReturn(BigInteger.valueOf(3)); // Status: ADMINISTRE
        when(mockTuple.getValue4()).thenReturn("0xinfirmier");
        when(mockTuple.getValue5()).thenReturn(BigInteger.valueOf(1703252000000L));

        when(supplyChain.lots(any(BigInteger.class)).send()).thenReturn(mockTuple);

        // Act
        BlockchainLotDto result = supplyChainGateway.getLotFromBlockchain(testLotId);

        // Assert
        assertEquals("ADMINISTRE", result.getStatusName());
        assertEquals(3, result.getBlockchainStatus());
    }

    @Test
    void testGetLotFromBlockchain_InvalidUUID() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(any(UserRole.class))).thenReturn(credentials);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            supplyChainGateway.getLotFromBlockchain("invalid-uuid");
        });
    }

    @Test
    void testStatusMapping_AllStates() throws Exception {
        // Arrange
        Credentials credentials = mock(Credentials.class);
        when(credentialsProvider.resolve(any(UserRole.class))).thenReturn(credentials);

        String[] expectedStatuses = {"CREE_PAR_GROSSISTE", "VALIDE_PAR_HOPITAL", "EN_STOCK_PHARMACIE", "ADMINISTRE"};

        for (int status = 0; status < 4; status++) {
            Tuple5<BigInteger, String, BigInteger, String, BigInteger> mockTuple = mock(Tuple5.class);
            when(mockTuple.getValue1()).thenReturn(BigInteger.ONE);
            when(mockTuple.getValue2()).thenReturn("TestLot");
            when(mockTuple.getValue3()).thenReturn(BigInteger.valueOf(status));
            when(mockTuple.getValue4()).thenReturn("0xactor");
            when(mockTuple.getValue5()).thenReturn(BigInteger.valueOf(1703252000000L));

            when(supplyChain.lots(any(BigInteger.class)).send()).thenReturn(mockTuple);

            // Act
            BlockchainLotDto result = supplyChainGateway.getLotFromBlockchain(testLotId);

            // Assert
            assertEquals(expectedStatuses[status], result.getStatusName());
        }
    }
}

package com.clinchain.backend.blockchain;

import com.clinchain.backend.dto.BlockchainLotDto;
import com.clinchain.backend.model.UserRole;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

@Component
@RequiredArgsConstructor
@Slf4j
public class SupplyChainGateway {

    private final Web3j web3j;
    private final ContractGasProvider gasProvider;
    private final BlockchainProperties properties;
    private final CredentialsProvider credentialsProvider;

    public TransactionReceipt creerLot(String lotId, String lotName, UserRole role) {
        return execute(role, contract -> contract.creerLot(toUint(lotId), lotName).send(),
                "creerLot", lotId);
    }

    public TransactionReceipt validerReceptionHopital(String lotId, UserRole role) {
        return execute(role, contract -> contract.validerReceptionHopital(toUint(lotId)).send(),
                "validerReceptionHopital", lotId);
    }

    public TransactionReceipt mettreEnPharmacie(String lotId, UserRole role) {
        return execute(role, contract -> contract.mettreEnPharmacie(toUint(lotId)).send(),
                "mettreEnPharmacie", lotId);
    }

    public TransactionReceipt administrerPatient(String lotId, UserRole role) {
        return execute(role, contract -> contract.administrerPatient(toUint(lotId)).send(),
                "administrerPatient", lotId);
    }

    public BlockchainLotDto getLotFromBlockchain(String lotId) {
        try {
            BigInteger lotUint = toUint(lotId);
            SupplyChain contract = loadContract(UserRole.GROSSISTE); // Read-only, any role works

            var result = contract.lots(lotUint).send();

            return BlockchainLotDto.builder()
                    .lotId(lotId)
                    .name(result.getValue2())
                    .blockchainStatus(result.getValue3().intValue())
                    .statusName(mapStatus(result.getValue3().intValue()))
                    .actor(result.getValue4())
                    .timestamp(result.getValue5().longValue())
                    .syncedWithDatabase(false) // Will be set by service
                    .build();
        } catch (Exception ex) {
            log.error("Failed to retrieve lot {} from blockchain", lotId, ex);
            throw new IllegalStateException("Failed to retrieve lot from blockchain: " + lotId, ex);
        }
    }

    private String mapStatus(int statusCode) {
        switch (statusCode) {
            case 0:
                return "CREE_PAR_GROSSISTE";
            case 1:
                return "VALIDE_PAR_HOPITAL";
            case 2:
                return "EN_STOCK_PHARMACIE";
            case 3:
                return "ADMINISTRE";
            default:
                return "UNKNOWN";
        }
    }

    private TransactionReceipt execute(UserRole role, ContractCall call, String functionName, String lotId) {
        try {
            SupplyChain contract = loadContract(role);
            TransactionReceipt receipt = call.apply(contract);
            log.debug("Executed {} on lot {} with tx {}", functionName, lotId, receipt.getTransactionHash());
            return receipt;
        } catch (Exception ex) {
            log.error("Blockchain transaction failed for {} on lot {}", functionName, lotId, ex);
            throw new IllegalStateException("Failed to execute " + functionName + " for lot " + lotId, ex);
        }
    }

    private SupplyChain loadContract(UserRole role) {
        Credentials credentials = credentialsProvider.resolve(role);
        TransactionManager txManager = createTransactionManager(credentials);
        return SupplyChain.load(properties.getContractAddress(), web3j, txManager, gasProvider);
    }

    private TransactionManager createTransactionManager(Credentials credentials) {
        Long chainId = properties.getChainId();
        if (chainId != null && chainId > 0) {
            return new RawTransactionManager(web3j, credentials, chainId.byteValue());
        }
        return new RawTransactionManager(web3j, credentials);
    }

    private BigInteger toUint(String lotId) {
        UUID uuid = UUID.fromString(lotId);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return new BigInteger(1, buffer.array());
    }

    @FunctionalInterface
    private interface ContractCall {
        TransactionReceipt apply(SupplyChain contract) throws Exception;
    }
}

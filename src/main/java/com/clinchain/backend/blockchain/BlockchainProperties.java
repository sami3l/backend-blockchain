package com.clinchain.backend.blockchain;

import com.clinchain.backend.model.UserRole;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blockchain")
@Getter
@Setter
public class BlockchainProperties {

    private String rpcUrl;
    private String contractAddress;
    private Long chainId;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private Map<UserRole, String> privateKeys = new EnumMap<>(UserRole.class);
}

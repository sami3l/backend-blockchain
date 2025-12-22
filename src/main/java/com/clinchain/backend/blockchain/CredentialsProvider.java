package com.clinchain.backend.blockchain;

import com.clinchain.backend.model.UserRole;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Credentials;

@Component
@RequiredArgsConstructor
public class CredentialsProvider {

    private final BlockchainProperties properties;

    public Credentials resolve(UserRole role) {
        Map<UserRole, String> keys = properties.getPrivateKeys();
        String privateKey = keys.get(role);
        if (!StringUtils.hasText(privateKey)) {
            throw new IllegalStateException("Missing private key for role: " + role);
        }
        return Credentials.create(privateKey);
    }
}

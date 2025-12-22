package com.clinchain.backend.blockchain;

import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

@Configuration
@RequiredArgsConstructor
public class BlockchainConfig {

    private final BlockchainProperties properties;

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(properties.getRpcUrl()));
    }

    @Bean
    public ContractGasProvider contractGasProvider() {
        BigInteger gasPrice = properties.getGasPrice() != null
                ? properties.getGasPrice()
                : DefaultGasProvider.GAS_PRICE;
        BigInteger gasLimit = properties.getGasLimit() != null
                ? properties.getGasLimit()
                : DefaultGasProvider.GAS_LIMIT;
        return new StaticGasProvider(gasPrice, gasLimit);
    }
}

package com.clinchain.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockchainLotDto {
    private String lotId;
    private String name;
    private int blockchainStatus;
    private String statusName;
    private String actor;
    private long timestamp;
    private boolean syncedWithDatabase;
}

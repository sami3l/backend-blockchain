package com.clinchain.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotStatsDto {
    private long totalLots;
    private long createdLots;
    private long validatedLots;
    private long inStockLots;
    private long administeredLots;
    private int totalQuantity;
}

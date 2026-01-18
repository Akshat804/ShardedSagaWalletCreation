package com.example.ShardedSagaWallet.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitWalletRequestDTO {
    private BigDecimal amount;
}
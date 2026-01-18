package com.example.ShardedSagaWallet.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponseDTO {
    private Long sagaInstanceId;
}
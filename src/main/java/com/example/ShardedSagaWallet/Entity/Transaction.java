package com.example.ShardedSagaWallet.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.shardingsphere.sql.parser.autogen.OracleStatementParser;
import org.apache.shardingsphere.transaction.api.TransactionType;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "transaction")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "from_wallet_id", nullable = false)
    private long fromWalletId;

    @Column(name = "to_wallet_id", nullable = false)
    private long toWalletId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactType type = TransactType.TRANSFER;

    @Column(name=" description")
    private String description;

    @Column(name = "saga_instance_id")
    private Long sagaInstanceId;
}




package com.example.ShardedSagaWallet.Entity;

public enum StepStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED,
    SKIPPED,
}

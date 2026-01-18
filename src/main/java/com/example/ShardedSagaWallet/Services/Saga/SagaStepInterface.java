package com.example.ShardedSagaWallet.Services.Saga;

public interface SagaStepInterface {
    boolean execute(SagaContext context);
    boolean compensate(SagaContext context);
    String getStepName();


}

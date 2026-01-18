package com.example.ShardedSagaWallet.Services.Saga;

import com.example.ShardedSagaWallet.Entity.SagaInstance;

public interface SagaOrchestrator {
    long startSaga(SagaContext context);
    boolean executeStep(long sagaInstanceId , String Stepname);
    boolean compensateStep(long sagaInstanceId, String stepName);
    SagaInstance getSagaInstance(long sagaInstanceId);
    void compensateSaga(long sagaInstanceId);
    void failSaga(long sagaInstanceId);
    void completeSaga(long sagaInstanceId);



}

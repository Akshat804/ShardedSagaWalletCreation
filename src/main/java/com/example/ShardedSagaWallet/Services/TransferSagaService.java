package com.example.ShardedSagaWallet.Services;

import com.example.ShardedSagaWallet.Entity.Transaction;
import com.example.ShardedSagaWallet.Services.Saga.SagaContext;
import com.example.ShardedSagaWallet.Services.Saga.SagaOrchestrator;
import com.example.ShardedSagaWallet.Services.Saga.Steps.SagaStepFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferSagaService {
    private final TransactionService transactionService;
    private final SagaOrchestrator sagaOrchestrator;


    @Transactional
    public Long initiateTransfer(
            Long fromWalletId,
            Long toWalletId,
            BigDecimal amount,
            String description
    ) {
        log.info("Initiating transfer from wallet {} to wallet {} with amount {} and description {}", fromWalletId, toWalletId, amount, description);

        Transaction transaction = transactionService.createTransaction(fromWalletId, toWalletId, amount, description);

        Map<String, Object> data = new java.util.HashMap<>();

        data.put("transactionId", transaction.getId());
        data.put("fromWalletId", fromWalletId);
        data.put("toWalletId", toWalletId);
        data.put("amount", amount);
        data.put("description", description); // null-safe

        SagaContext sagaContext = SagaContext.builder()
                .data(data)
                .build();

        log.info("Saga context created");

        Long sagaInstanceId = sagaOrchestrator.startSaga(sagaContext);
        log.info("Saga instance created with id {}", sagaInstanceId);

        transactionService.updateTransactionWithSagaInstanceId(transaction.getId(), sagaInstanceId);

        executeTransferSaga(sagaInstanceId);

        return sagaInstanceId;
    }

    public void executeTransferSaga(Long sagaInstanceId) {
        log.info("Executing transfer saga with id {}", sagaInstanceId);


        try {
            for(SagaStepFactory.SagaStepType step : SagaStepFactory.TransferMoneySagaSteps) {
                boolean success  = sagaOrchestrator.executeStep(sagaInstanceId, step.toString() );
                if(!success) {
                    log.error("Failed to execute step {}", step.toString());
                    sagaOrchestrator.failSaga(sagaInstanceId);
                    sagaOrchestrator.compensateSaga(sagaInstanceId);
                    return;
                }

            }
            sagaOrchestrator.completeSaga(sagaInstanceId);
            log.info("Transfer saga completed with id {}", sagaInstanceId);
        } catch (Exception e) {
            log.error("Failed to execute transfer saga with id {}", sagaInstanceId, e);
            sagaOrchestrator.failSaga(sagaInstanceId);

        }
    }

}

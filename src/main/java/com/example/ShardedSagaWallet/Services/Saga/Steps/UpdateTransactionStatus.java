package com.example.ShardedSagaWallet.Services.Saga.Steps;

import com.example.ShardedSagaWallet.Entity.Transaction;
import com.example.ShardedSagaWallet.Entity.TransactionStatus;
import com.example.ShardedSagaWallet.Services.Saga.SagaContext;
import com.example.ShardedSagaWallet.Services.Saga.SagaStepInterface;
import com.example.ShardedSagaWallet.repositories.TransactionRepository;
import com.example.ShardedSagaWallet.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpdateTransactionStatus implements SagaStepInterface {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Override
    public boolean execute(SagaContext context){
      Long transactionId=context.getLong("transactionId");
      log.info("updating the status for transaction{}",transactionId);
        Transaction transaction=transactionRepository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction id not found"));

        context.put("orignalTransactionStatus", transaction.getStatus());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);






        return true;
    }
    @Override
    public boolean compensate(SagaContext context){
        Long transactionId=context.getLong("transactionId");
        log.info("updating the status for compensating transaction{}",transactionId);
        Transaction transaction=transactionRepository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction id not found"));

        context.put("orignalTransactionStatus", transaction.getStatus());
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);
        return true;
    }
    @Override
    public String getStepName(){
        return null;
    }


}

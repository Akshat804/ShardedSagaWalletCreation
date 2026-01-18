package com.example.ShardedSagaWallet.Config;

import com.example.ShardedSagaWallet.Services.Saga.SagaStepInterface;
import com.example.ShardedSagaWallet.Services.Saga.Steps.CreditDestinationWalletStepInterface;
import com.example.ShardedSagaWallet.Services.Saga.Steps.DebitSourceWalletStepInterface;
import com.example.ShardedSagaWallet.Services.Saga.Steps.SagaStepFactory;
import com.example.ShardedSagaWallet.Services.Saga.Steps.UpdateTransactionStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SagaConfiguration {
    @Bean
    public Map<String,SagaStepInterface> sagaStepMap(DebitSourceWalletStepInterface debitSourceWalletStep,
                                                      CreditDestinationWalletStepInterface creditDestinationWalletStep,
                                                      UpdateTransactionStatus updateTransactionStatus){
        Map<String, SagaStepInterface> sagaStepMap=new HashMap<>();
        sagaStepMap.put(SagaStepFactory.SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString(),debitSourceWalletStep);
        sagaStepMap.put(SagaStepFactory.SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString(),creditDestinationWalletStep);
        sagaStepMap.put(SagaStepFactory.SagaStepType.UPDATE_TRANSACTION_STATUS_STEP.toString(),updateTransactionStatus);
         return sagaStepMap;



    }


}

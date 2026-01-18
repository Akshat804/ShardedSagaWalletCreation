package com.example.ShardedSagaWallet.Services.Saga.Steps;

import com.example.ShardedSagaWallet.Entity.Wallet;
import com.example.ShardedSagaWallet.Services.Saga.SagaContext;
import com.example.ShardedSagaWallet.Services.Saga.SagaStepInterface;
import com.example.ShardedSagaWallet.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditDestinationWalletStepInterface implements SagaStepInterface {
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext context){
        //step1:get dest wallet id
        //step 2:fetch dest wallet from db with alock
        //step3 cred the dest wallet
        // step4:update the context with changes
     Long toWalletId=context.getLong("toWalletId");
        BigDecimal amount=context.getBiGDecimal("amount");
        log.info("Crediting destination wallet {} with amount {}",toWalletId,amount);

        Wallet wallet=walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(()->new RuntimeException("WALLET NOT FOUND"));
        log.info("wallet fetched with balance{}",wallet.getBalance());
        context.put("orignalToWalletBalance",wallet.getBalance());


        wallet.credit(amount);
        walletRepository.save(wallet);
        log.info("wallet saved with balance{}",wallet.getBalance());
        context.put("toWalletBalanceAfterCredit",wallet.getBalance());

        return true;






    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context){
        Long toWalletId=context.getLong("toWalletId");
        BigDecimal amount=context.getBiGDecimal("amount");
        log.info("Compensating credit of  destination wallet {} with amount {}",toWalletId,amount);

        Wallet wallet=walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(()->new RuntimeException("WALLET NOT FOUND"));
        log.info("wallet fetched with balance{}",wallet.getBalance());
        context.put("orignalToWalletBalance",wallet.getBalance());


        wallet.debit(amount);
        walletRepository.save(wallet);
        log.info("wallet saved with balance{}",wallet.getBalance());
        context.put("toWalletBalanceAfterCreditCompensation",wallet.getBalance());



        return true;

    }

    @Override
    public String getStepName(){
       return SagaStepFactory.SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString();
    }


}

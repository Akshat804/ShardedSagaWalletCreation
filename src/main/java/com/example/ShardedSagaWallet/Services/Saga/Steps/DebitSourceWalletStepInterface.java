package com.example.ShardedSagaWallet.Services.Saga.Steps;

import com.example.ShardedSagaWallet.Entity.Wallet;
import com.example.ShardedSagaWallet.Services.Saga.SagaContext;
import com.example.ShardedSagaWallet.Services.Saga.SagaStepInterface;
import com.example.ShardedSagaWallet.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
@Slf4j
public class DebitSourceWalletStepInterface extends com.example.ShardedSagaWallet.Entity.SagaStep implements SagaStepInterface {
     private final WalletRepository walletRepository;

    @Override
    public boolean execute(SagaContext context){
        Long fromWalletID=context.getLong("fromWalletId");
        BigDecimal amount=context.getBiGDecimal("amount");

        log.info("Debiting source wallet{}with amount{}",fromWalletID, amount);
        Wallet wallet=walletRepository.findByIdWithLock(fromWalletID)
        .orElseThrow(()->new RuntimeException("wallet not found"));
        log.info("Wallet fetched with balance {}",wallet.getBalance());
        context.put("orignalSourceWalletBalance", wallet.getBalance());
        wallet.debit(amount);
        walletRepository.save(wallet);
        log.info("wallet saved with balance{}",wallet.getBalance());
        context.put("sourceWalletBalanceAfterDebit",wallet.getBalance());
        log.info("done successfully");

        return true;
    }
    @Override
    public boolean compensate(SagaContext context){
        Long fromWalletID=context.getLong("fromWalletID");
        BigDecimal amount=context.getBiGDecimal("amount");

        log.info("Compensating Debit source wallet{}with amount{}",fromWalletID, amount);
        Wallet wallet=walletRepository.findByIdWithLock(fromWalletID)
                .orElseThrow(()->new RuntimeException("wallet not found"));
        log.info("Wallet fetched with balance {}",wallet.getBalance());
        context.put("SourceWalletBalanceBeforeCompensation", wallet.getBalance());
        wallet.credit(amount);
        walletRepository.save(wallet);
        log.info("wallet saved with balance{}",wallet.getBalance());
        context.put("sourceWalletBalanceAfterCreditCompensation",wallet.getBalance());
        log.info("done successfully");

        return true;
    }
    @Override
    public String getStepName(){
   return SagaStepFactory.SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString();
    }

}

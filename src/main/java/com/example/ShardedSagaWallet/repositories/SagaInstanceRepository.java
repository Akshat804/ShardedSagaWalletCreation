package com.example.ShardedSagaWallet.repositories;

import com.example.ShardedSagaWallet.Entity.SagaInstance;
import com.example.ShardedSagaWallet.Entity.SagaStep;
import com.example.ShardedSagaWallet.Entity.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {


}

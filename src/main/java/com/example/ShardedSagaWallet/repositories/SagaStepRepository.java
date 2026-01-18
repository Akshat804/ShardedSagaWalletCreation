package com.example.ShardedSagaWallet.repositories;

import com.example.ShardedSagaWallet.Entity.SagaStep;
import com.example.ShardedSagaWallet.Entity.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep,Long> {
    List<SagaStep>findBySagaInstanceId(long sagaInstanceId);
    List<SagaStep> findBySagaInstanceIdAndStatus(long sagaInstanceId, StepStatus status);
    Optional<SagaStep> findBySagaInstanceIdAndStepNameAndStatus(long sagaInstanceId,String Stepname,StepStatus status);
    List<SagaStep> findBySagaInstanceIdAndStatus(Long sagaInstanceId, StepStatus status);

}

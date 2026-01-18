package com.example.ShardedSagaWallet.Services.Saga;

import com.example.ShardedSagaWallet.Entity.SagaInstance;
import com.example.ShardedSagaWallet.Entity.SagaStatus;
import com.example.ShardedSagaWallet.Entity.SagaStep;
import com.example.ShardedSagaWallet.Entity.StepStatus;
import com.example.ShardedSagaWallet.Services.Saga.Steps.SagaStepFactory;
import com.example.ShardedSagaWallet.repositories.SagaInstanceRepository;
import com.example.ShardedSagaWallet.repositories.SagaStepRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.ShardedSagaWallet.Entity.SagaStatus.COMPLETED;
import static com.example.ShardedSagaWallet.Entity.SagaStatus.FAILED;

@Service
@RequiredArgsConstructor
@Builder
@Slf4j
@Setter
public class SagaOrchestratorImpl implements SagaOrchestrator{
    private final ObjectMapper objectMapper;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepFactory sagaStepFactory;
    private final SagaStepRepository sagaStepRepository;
    @Override
    @Transactional
   public long startSaga(SagaContext context){

        try{
            String contextJson= objectMapper.writeValueAsString(context);
            SagaInstance sagaInstance=SagaInstance
                    .builder()
                    .context(contextJson)
                    .status(SagaStatus.STARTED)
                    .build();
            sagaInstance= sagaInstanceRepository.save(sagaInstance);
            return sagaInstance.getId();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    @Transactional
   public  boolean executeStep(long sagaInstanceId , String Stepname){
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(()-> new RuntimeException("saga instance not found"));
        SagaStepInterface stepInterface = sagaStepFactory.getSagaStep(Stepname);

        if(stepInterface ==null){
            log.error("Saga step not found for name {}",Stepname);
            throw new RuntimeException("SAGA STEP NOT FOUND");

        }

        SagaStep sagaStepDB=sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId,Stepname,StepStatus.PENDING)
                .stream()
                .filter(s -> s.getStepName().equals(Stepname))
                .findFirst()
                .orElse(SagaStep.builder().sagaInstanceId(sagaInstanceId).stepName(Stepname).status(StepStatus.PENDING).build());

        if(sagaStepDB.getId()==null){
            sagaStepDB=sagaStepRepository.save(sagaStepDB);
        }
        try{
            SagaContext sagaContext=objectMapper.readValue(sagaInstance.getContext(),SagaContext.class);
            sagaStepDB.setStatus(StepStatus.RUNNING);
            sagaStepRepository.save(sagaStepDB);
            boolean success=stepInterface.execute(sagaContext);
            if(success){
                sagaStepDB.setStatus( StepStatus.COMPLETED);
                sagaStepRepository.save(sagaStepDB);
                log.info("step {} executed successfully",Stepname);
                return true;
            }
            else{
                sagaStepDB.setStatus(StepStatus.PENDING);
                sagaStepRepository.save(sagaStepDB);
                log.info("Saga step {} failed",Stepname);
                return false;

            }

        }
        catch (Exception e){
            sagaStepDB.setStatus(StepStatus.PENDING);
            sagaStepRepository.save(sagaStepDB);
            log.info("Saga step {} failed",Stepname);
            return false;
        }
    }
    @Override
    @Transactional
   public  boolean compensateStep(long sagaInstanceId, String Stepname){
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(()-> new RuntimeException("saga instance not found"));
        SagaStepInterface stepInterface = sagaStepFactory.getSagaStep(Stepname);

        if(stepInterface ==null){
            log.error("Saga step not found for name {}",Stepname);
            throw new RuntimeException("SAGA STEP NOT FOUND");

        }

        SagaStep sagaStepDB=sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId,Stepname,StepStatus.COMPLETED)
                .orElse(null);

        if(sagaStepDB.getId()==null){
            log.info("step {} not found for sagainstance {} so it is already compensated or not executed",Stepname,sagaInstanceId);

            return true;

        }
        try{
            SagaContext sagaContext=objectMapper.readValue(sagaInstance.getContext(),SagaContext.class);
            sagaStepDB.setStatus(StepStatus.COMPENSATING);
            sagaStepRepository.save(sagaStepDB);
            boolean success=stepInterface.compensate(sagaContext);
            if(success){
                sagaStepDB.setStatus( StepStatus.COMPENSATED);
                sagaStepRepository.save(sagaStepDB);
                log.info("step {} executed successfully",Stepname);
                return true;
            }
            else{
                sagaStepDB.setStatus(StepStatus.FAILED);
                sagaStepRepository.save(sagaStepDB);
                log.info("Saga step {} failed",Stepname);
                return false;

            }

        }
        catch (Exception e){
            sagaStepDB.setStatus(StepStatus.FAILED);
            sagaStepRepository.save(sagaStepDB);
            log.info("Saga step {} failed TO COMPENSATE ",Stepname);
            return false;
        }

        }
    @Override
    @Transactional
  public  SagaInstance getSagaInstance(long sagaInstanceId){
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(()-> new RuntimeException("saga instance not found"));

        return sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(() -> new RuntimeException("saga instance not found"));
    }
    @Override
   public void compensateSaga(long sagaInstanceId){
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(()->new RuntimeException("Saga Instance not found"));
        sagaInstance.setStatus(SagaStatus.COMPENSATING);
        sagaInstanceRepository.save(sagaInstance);
        List<SagaStep>completedSteps= sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId,StepStatus.COMPLETED );
        boolean allcompensated=true;
        for(SagaStep completedstep:completedSteps){
            boolean compensated=this.compensateStep(sagaInstanceId,completedstep.getStepName());
            if(!compensated){
                allcompensated=false;
            }

        }
        if(allcompensated){
            sagaInstance.setStatus(COMPLETED);
            sagaInstanceRepository.save(sagaInstance);
            log.info("SAGA {} compensated  successfully",sagaInstanceId);

        }
        else{
            log.error("Saga ccompensated partially",sagaInstanceId);

        }




    }
    @Override
    @Transactional
   public  void failSaga(long sagaInstanceId){
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(()->new RuntimeException("Saga Instance not found"));
        sagaInstance.setStatus(FAILED);
        sagaInstanceRepository.save(sagaInstance);
    }
    @Override
    @Transactional
   public  void completeSaga(long sagaInstanceId){
        SagaInstance sagaInstance=sagaInstanceRepository.findById(sagaInstanceId).orElseThrow(()->new RuntimeException("Saga Instance not found"));
        sagaInstance.setStatus(COMPLETED);
        sagaInstanceRepository.save(sagaInstance);
    }



}

package com.example.ShardedSagaWallet.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="saga_step")
@Data
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SagaStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @Column(name = "saga_instance_id",nullable =false)
    private long sagaInstanceId;
   @Column(name = "step_name",nullable=false)
    private String stepName;
    @Column(name = "status",nullable = false)
    private StepStatus status;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "step_data",columnDefinition="json")
    private String stepData;


}

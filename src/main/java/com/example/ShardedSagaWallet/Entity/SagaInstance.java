package com.example.ShardedSagaWallet.Entity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.calcite.model.JsonType;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="saga_instance")
public class SagaInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable=false)
    @Builder.Default
    private SagaStatus status=SagaStatus.STARTED;
    @JsonSubTypes.Type(JsonType.class)
    @Column(name = "context",columnDefinition = "TEXT")
    private String context;

    @Column(name = "current_step")
    private String currentStep;





}

package com.example.ShardedSagaWallet.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name= "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Wallet {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   @Column(name = "user_id",nullable=false,updatable=false)
    private Long userId;
   @Column(name = "is_active",nullable =false)
    private Boolean isActive;
   @Column(name = "balance",nullable =false)
   @Builder.Default
    private BigDecimal balance=BigDecimal.ZERO;

   public boolean hasSufficientBalance(BigDecimal amount) {
       return balance.compareTo(amount) >= 0;
   }

   public void debit(BigDecimal amount) {
       if(!hasSufficientBalance(amount)){
           throw new RuntimeException("Insufficient balance");
       }
       balance = balance.subtract(amount);
   }
   public void credit(BigDecimal amount) {
       balance = balance.add(amount);
   }


}

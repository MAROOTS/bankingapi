package com.example.banking.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TransactionType type;
        @Column(nullable = false,precision = 19,scale = 2)
        private BigDecimal amount;
        @Column(precision = 19,scale = 2)
        private BigDecimal balanceAfter;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "source_account_id")
        private BankAccount sourceAccount;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "destination_account_id")
        private BankAccount destinationAccount;
        private String description;
        @Enumerated(EnumType.STRING)
        private TransactionStatus status;
        private LocalDateTime createdAt;
        @PrePersist
        protected void onCreate(){
                createdAt = LocalDateTime.now();
                if (status == null) status = TransactionStatus.SUCCESS;
        }
        public enum TransactionType{
                DEPOSIT,WITHDRAWAL,TRANSFER
        }
        public enum TransactionStatus{
                SUCCESS,FAILED,FLAGGED
        }


}

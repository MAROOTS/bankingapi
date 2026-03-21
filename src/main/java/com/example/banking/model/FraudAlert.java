package com.example.banking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "fraud_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="account_id")
    private BankAccount account;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudReason reason;
    @Enumerated(EnumType.STRING)
    private FraudStatus status;
    private String details;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        if (status==null) status = FraudStatus.OPEN;
    }

    public enum FraudReason{
            LARGE_TRANSACTION,
            MULTIPLE_RAPID_TRANSFERS,
            UNUSUAL_LATE_NIGHT,
            RAPID_BALANCE_DRAIN
    }
    public enum FraudStatus{
        OPEN,
        REVIEWED,
        RESOLVED
    }
}

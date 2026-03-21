package com.example.banking.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "interest_configs")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class InterestConfig {
    // stores interest rate per account type
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    @Column(unique = true,nullable = false)
    private BankAccount.AccountType accountType;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualInterestRate;
    private boolean active;
    private LocalDateTime updatedAt;
    @PrePersist
    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}

package com.example.banking.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true,nullable = false)
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    @Column(nullable = false,precision = 19,scale = 2)
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User owner;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        if (balance == null) balance = BigDecimal.ZERO;
        if (status == null) status = AccountStatus.ACTIVE;
    }

    public enum AccountType{
        SAVINGS, CHECKING, FIXED_DEPOSIT
    }

    public enum AccountStatus{
        ACTIVE, SUSPENDED, CLOSED
    }

}

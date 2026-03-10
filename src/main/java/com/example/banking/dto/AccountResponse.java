package com.example.banking.dto;

import com.example.banking.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AccountResponse {
    private String id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;

    public static AccountResponse from(BankAccount account){
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .ownerName(account.getOwner().getFullName())
                .ownerEmail(account.getOwner().getEmail())
                .createdAt(account.getCreatedAt())
                .build();
    }

}

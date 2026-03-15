package com.example.banking.dto;

import com.example.banking.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor

public class TransactionResponse {
    private String id;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String sourceAccount;
    private String destinationAccount;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .type(t.getType().name())
                .amount(t.getAmount())
                .balanceAfter(t.getBalanceAfter())
                .sourceAccount(t.getSourceAccount() != null ?
                        t.getSourceAccount().getAccountNumber() : null)
                .destinationAccount(t.getDestinationAccount() != null ?
                        t.getDestinationAccount().getAccountNumber() : null)
                .description(t.getDescription())
                .status(t.getStatus().name())
                .createdAt(t.getCreatedAt())
                .build();
    }
}

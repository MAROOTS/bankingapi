package com.example.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class TransactionSummaryResponse {
    private String accountNumber;
    private BigDecimal currentBalance;
    private long totalTransactions;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalTransfersOut;
}

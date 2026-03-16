package com.example.banking.dto;

import com.example.banking.model.Transaction;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class TransactionFilterRequest {
    // Filter by transaction type e.g. DEPOSIT, WITHDRAWAL, TRANSFER
    private Transaction.TransactionType type;

    // Filter by status e.g. SUCCESS, FAILED, FLAGGED
    private Transaction.TransactionStatus status;

    // Filter by amount range
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    // Filter by date range
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    // Sorting
    private String sortBy = "createdAt";      // default sort field
    private String sortDirection = "DESC";     // default sort direction
}

package com.example.banking.dto;

import com.example.banking.model.FraudAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@Builder
public class FraudAlertResponse {
    private String id;
    private String transactionId;
    private String accountNumber;
    private String reason;
    private String details;
    private String status;
    private LocalDateTime createdAt;

    public static FraudAlertResponse from(FraudAlert alert) {
        return FraudAlertResponse.builder()
                .id(alert.getId())
                .transactionId(alert.getTransaction() != null ?
                        alert.getTransaction().getId() : null)
                .accountNumber(alert.getAccount().getAccountNumber())
                .reason(alert.getReason().name())
                .details(alert.getDetails())
                .status(alert.getStatus().name())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}

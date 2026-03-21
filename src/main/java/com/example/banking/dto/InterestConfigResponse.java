package com.example.banking.dto;

import com.example.banking.model.InterestConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Builder
@Data
@AllArgsConstructor
public class InterestConfigResponse {
    private String id;
    private String accountType;
    private BigDecimal annualInterestRate;
    private BigDecimal monthlyInterestRate;
    private boolean active;
    private LocalDateTime updatedAt;

    public static InterestConfigResponse from(InterestConfig config){
        return InterestConfigResponse.builder()
                .id(config.getId())
                .accountType(config.getAccountType().name())
                .annualInterestRate(config.getAnnualInterestRate())
                .monthlyInterestRate(
                        config.getAnnualInterestRate()
                                .divide(java.math.BigDecimal.valueOf(12), 4,
                                        java.math.RoundingMode.HALF_UP))
                .active(config.isActive())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}

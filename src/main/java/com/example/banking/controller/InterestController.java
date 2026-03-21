package com.example.banking.controller;

import com.example.banking.dto.InterestConfigResponse;
import com.example.banking.model.BankAccount;
import com.example.banking.service.InterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
public class InterestController {
    private final InterestService interestService;

    // Get all interest rate configs
    @GetMapping("/configs")
    public ResponseEntity<List<InterestConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(interestService.getAllConfigs());
    }

    // Set interest rate for an account type
    @PostMapping("/configs/{accountType}")
    public ResponseEntity<InterestConfigResponse> setInterestRate(
            @PathVariable BankAccount.AccountType accountType,
            @RequestParam BigDecimal rate) {
        return ResponseEntity.ok(interestService.setInterestRate(accountType, rate));
    }

    // Manually trigger interest (for testing)
    @PostMapping("/trigger")
    public ResponseEntity<String> triggerInterest() {
        return ResponseEntity.ok(interestService.triggerInterestManually());
    }
}


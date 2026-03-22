package com.example.banking.controller;

import com.example.banking.dto.InterestConfigResponse;
import com.example.banking.model.BankAccount;
import com.example.banking.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
@Tag(name = "Interest", description = "Manage interest rates and apply interest")
@SecurityRequirement(name = "Bearer Authentication")
public class InterestController {
    private final InterestService interestService;

    // Get all interest rate configs
    @Operation(summary = "Get all interest rate configurations")
    @GetMapping("/configs")
    public ResponseEntity<List<InterestConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(interestService.getAllConfigs());
    }

    // Set interest rate for an account type

    @Operation(summary = "Set interest rate for an account type")
    @PostMapping("/configs/{accountType}")
    public ResponseEntity<InterestConfigResponse> setInterestRate(
            @PathVariable BankAccount.AccountType accountType,
            @RequestParam BigDecimal rate) {
        return ResponseEntity.ok(interestService.setInterestRate(accountType, rate));
    }

    // Manually trigger interest (for testing)

    @Operation(summary = "Manually trigger monthly interest calculation")
    @PostMapping("/trigger")
    public ResponseEntity<String> triggerInterest() {
        return ResponseEntity.ok(interestService.triggerInterestManually());
    }
}


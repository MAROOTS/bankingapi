package com.example.banking.controller;

import com.example.banking.dto.FraudAlertResponse;
import com.example.banking.model.FraudAlert;
import com.example.banking.service.FraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Tag(name = "Fraud Detection", description = "View and manage fraud alerts")
@SecurityRequirement(name = "Bearer Authentication")
public class FraudController {
    private final FraudDetectionService fraudDetectionService;


    @Operation(summary = "Get all open fraud alerts")
    @GetMapping("/alerts")
    public ResponseEntity<List<FraudAlertResponse>> getOpenAlerts() {
        return ResponseEntity.ok(fraudDetectionService.getOpenAlerts());
    }


    @Operation(summary = "Get fraud alerts for a specific account")
    @GetMapping("/alerts/account/{accountId}")
    public ResponseEntity<List<FraudAlertResponse>> getAlertsByAccount(
            @PathVariable String accountId) {
        return ResponseEntity.ok(
                fraudDetectionService.getAlertsByAccount(accountId));
    }


    @Operation(summary = "Update fraud alert status")
    @PatchMapping("/alerts/{alertId}")
    public ResponseEntity<FraudAlertResponse> updateAlertStatus(
            @PathVariable String alertId,
            @RequestParam FraudAlert.FraudStatus status) {
        return ResponseEntity.ok(
                fraudDetectionService.updateAlertStatus(alertId, status));
    }
}

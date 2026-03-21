package com.example.banking.controller;

import com.example.banking.dto.FraudAlertResponse;
import com.example.banking.model.FraudAlert;
import com.example.banking.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
public class FraudController {
    private final FraudDetectionService fraudDetectionService;

    @GetMapping("/alerts")
    public ResponseEntity<List<FraudAlertResponse>> getOpenAlerts() {
        return ResponseEntity.ok(fraudDetectionService.getOpenAlerts());
    }

    @GetMapping("/alerts/account/{accountId}")
    public ResponseEntity<List<FraudAlertResponse>> getAlertsByAccount(
            @PathVariable String accountId) {
        return ResponseEntity.ok(
                fraudDetectionService.getAlertsByAccount(accountId));
    }

    @PatchMapping("/alerts/{alertId}")
    public ResponseEntity<FraudAlertResponse> updateAlertStatus(
            @PathVariable String alertId,
            @RequestParam FraudAlert.FraudStatus status) {
        return ResponseEntity.ok(
                fraudDetectionService.updateAlertStatus(alertId, status));
    }
}

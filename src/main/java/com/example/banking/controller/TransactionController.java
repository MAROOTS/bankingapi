package com.example.banking.controller;

import com.example.banking.dto.DepositWithdrawRequest;
import com.example.banking.dto.TransactionResponse;
import com.example.banking.dto.TransferRequest;
import com.example.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody DepositWithdrawRequest request) {
        return ResponseEntity.ok(transactionService.deposit(email, request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody DepositWithdrawRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(email, request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(email, request));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @AuthenticationPrincipal String email,
            @PathVariable String accountId) {
        return ResponseEntity.ok(
                transactionService.getAccountTransactions(accountId, email));
    }
}
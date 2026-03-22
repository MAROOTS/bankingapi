package com.example.banking.controller;

import com.example.banking.dto.*;
import com.example.banking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Deposit, withdraw, transfer and history")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Deposit money into an account")
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody DepositWithdrawRequest request) {
        return ResponseEntity.ok(transactionService.deposit(email, request));
    }

    @Operation(summary = "Withdraw money from an account")
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody DepositWithdrawRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(email, request));
    }

    @Operation(summary = "Transfer money between two accounts")
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(email, request));
    }

    @Operation(summary = "Get all transactions for an account")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @AuthenticationPrincipal String email,
            @PathVariable String accountId) {
        return ResponseEntity.ok(
                transactionService.getAccountTransactions(accountId, email));
    }


    @Operation(summary = "Get filtered transactions for an account")
    @GetMapping("/account/{accountId}/filter")
    public ResponseEntity<List<TransactionResponse>> getFilteredTransactions(
            @AuthenticationPrincipal String email,
            @PathVariable String accountId,
            @ModelAttribute TransactionFilterRequest filter) {
        return ResponseEntity.ok(
                transactionService.getFilteredTransactions(accountId, email, filter));
    }


    @Operation(summary = "Get transaction summary for an account")
    @GetMapping("/account/{accountId}/summary")
    public ResponseEntity<TransactionSummaryResponse> getAccountSummary(
            @AuthenticationPrincipal String email,
            @PathVariable String accountId) {
        return ResponseEntity.ok(
                transactionService.getAccountSummary(accountId, email));
    }
}
package com.example.banking.controller;

import com.example.banking.dto.AccountResponse;
import com.example.banking.dto.CreateAccountRequest;
import com.example.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Bank Accounts", description = "Create and manage bank accounts")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {
    private final AccountService accountService;

    @Operation(summary = "Create a new bank account")
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.ok(accountService.createAccount(email, request));
    }

    @Operation(summary = "Get all accounts for logged in user")
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(accountService.getMyAccounts(email));
    }

    @Operation(summary = "Get a specific account by ID")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @AuthenticationPrincipal String email,
            @PathVariable String accountId) {
        return ResponseEntity.ok(accountService.getAccountById(accountId, email));
    }

    @Operation(summary = "Close a bank account")
    @PatchMapping("/{accountId}/close")
    public ResponseEntity<AccountResponse> closeAccount(
            @AuthenticationPrincipal String email,
            @PathVariable String accountId) {
        return ResponseEntity.ok(accountService.closeAccount(accountId, email));
    }
}


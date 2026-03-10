package com.example.banking.dto;

import com.example.banking.model.BankAccount;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotNull(message = "Account Type required")
    private BankAccount.AccountType accountType;
}

package com.example.banking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotNull(message = "Active status required")
    private boolean active;
}

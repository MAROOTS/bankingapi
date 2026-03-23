package com.example.banking.dto;

import com.example.banking.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
public class AdminUserResponse {
    private String id;
    private String fullName;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    private int totalAccounts;
    private List<AccountResponse> accounts;

    public static AdminUserResponse from(User user, List<AccountResponse> accounts) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .totalAccounts(accounts.size())
                .accounts(accounts)
                .build();
    }
}

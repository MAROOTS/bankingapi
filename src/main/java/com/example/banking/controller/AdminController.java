package com.example.banking.controller;

import com.example.banking.dto.AdminUserResponse;
import com.example.banking.dto.UpdateUserStatusRequest;
import com.example.banking.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin user and platform management")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "Get all users with their accounts")
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation(summary = "Get a specific user by ID")
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable String userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @Operation(summary = "Activate or deactivate a user")
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(
                adminService.updateUserStatus(userId, request.isActive()));
    }

    @Operation(summary = "Promote a user to ADMIN role")
    @PatchMapping("/users/{userId}/promote")
    public ResponseEntity<AdminUserResponse> promoteToAdmin(
            @PathVariable String userId) {
        return ResponseEntity.ok(adminService.promoteToAdmin(userId));
    }

    @Operation(summary = "Suspend all accounts of a user")
    @PatchMapping("/users/{userId}/suspend-accounts")
    public ResponseEntity<String> suspendUserAccounts(
            @PathVariable String userId) {
        adminService.suspendUserAccounts(userId);
        return ResponseEntity.ok("All accounts suspended successfully");
    }

    @Operation(summary = "Get platform statistics")
    @GetMapping("/stats")
    public ResponseEntity<AdminService.AdminStatsResponse> getPlatformStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }
}

package com.example.banking.service;

import com.example.banking.dto.AccountResponse;
import com.example.banking.dto.AdminUserResponse;
import com.example.banking.model.BankAccount;
import com.example.banking.model.User;
import com.example.banking.repository.BankAccountRepository;
import com.example.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BankAccountRepository accountRepository;

    public List<AdminUserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    List<AccountResponse> accounts = accountRepository
                            .findByOwnerId(user.getId())
                            .stream()
                            .map(AccountResponse::from)
                            .collect(Collectors.toList());
                    return AdminUserResponse.from(user,accounts);
                })
                .collect(Collectors.toList());
    }

    public AdminUserResponse getUserById(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        List<AccountResponse> accounts = accountRepository
                .findByOwnerId(userId)
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());

        return AdminUserResponse.from(user,accounts);
    }

    @Transactional
    public AdminUserResponse updateUserStatus(String userId, boolean active){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));
        user.setActive(active);
        userRepository.save(user);

        List<AccountResponse> accounts = accountRepository
                .findByOwnerId(userId)
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());

        return AdminUserResponse.from(user,accounts);
    }

    @Transactional
    public AdminUserResponse promoteToAdmin(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        user.setRole(User.Role.ADMIN);
        userRepository.save(user);

        List<AccountResponse> accounts = accountRepository
                .findByOwnerId(userId)
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
        return AdminUserResponse.from(user,accounts);
    }

    @Transactional
    public void suspendUserAccounts(String userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        List<BankAccount> accounts = accountRepository.findByOwnerId(userId);
        accounts.forEach(account ->
                    account.setStatus(BankAccount.AccountStatus.SUSPENDED)
                );
        accountRepository.saveAll(accounts);
    }

    public AdminStatsResponse getPlatformStats(){
        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long activeAccounts = accountRepository.countByStatus(BankAccount.AccountStatus.ACTIVE);
        long suspendedAccounts = accountRepository.countByStatus(BankAccount.AccountStatus.SUSPENDED);
        return new AdminStatsResponse(
                totalUsers, totalAccounts,
                activeAccounts, suspendedAccounts);
    }
    public record AdminStatsResponse(
      long totalUsers,
      long totalAccounts,
      long activeAccounts,
      long suspendedAccounts
    ){}
}

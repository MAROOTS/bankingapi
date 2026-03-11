package com.example.banking.service;

import com.example.banking.dto.AccountResponse;
import com.example.banking.dto.CreateAccountRequest;
import com.example.banking.model.BankAccount;
import com.example.banking.model.User;
import com.example.banking.repository.BankAccountRepository;
import com.example.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final BankAccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountResponse createAccount(String email, CreateAccountRequest request){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        BankAccount account = BankAccount.builder()
                .accountNumber(accountNumberGenerator.generate())
                .accountType(request.getAccountType())
                .owner(user)
                .build();
        accountRepository.save(account);
        return AccountResponse.from(account);
    }
    public List<AccountResponse> getMyAccounts(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));
        return accountRepository.findByOwnerId(user.getId())
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(String accountId,String email){
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(()->new RuntimeException("Account not found"));

        if (!account.getOwner().getEmail().equals(email)){
            throw new RuntimeException("Access denied!");
        }
        return AccountResponse.from(account);
    }

    public AccountResponse closeAccount(String accountId,String email){
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(()->new RuntimeException("Access not found!"));
        if (!account.getOwner().getEmail().equals(email)){
            throw new RuntimeException("Access denied!");
        }
        account.setStatus(BankAccount.AccountStatus.CLOSED);
        accountRepository.save(account);
        return AccountResponse.from(account);
    }

}

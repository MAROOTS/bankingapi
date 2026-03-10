package com.example.banking.service;

import com.example.banking.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {
    private final BankAccountRepository accountRepository;

    public  String generate(){
        String accountNumber;
        do {
            accountNumber = generateRandom();
        }while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
    private String generateRandom(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder("BNK");
        for (int i =0; i < 9; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}

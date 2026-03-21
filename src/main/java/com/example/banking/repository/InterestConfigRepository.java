package com.example.banking.repository;

import com.example.banking.model.BankAccount;
import com.example.banking.model.InterestConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestConfigRepository extends JpaRepository<InterestConfig, String> {
    Optional<InterestConfig> findByAccountType(BankAccount.AccountType accountType);
    List<InterestConfig> findByActiveTrue();
}

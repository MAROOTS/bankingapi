package com.example.banking.repository;

import com.example.banking.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
    List<BankAccount> findByOwnerId(String ownerId);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}

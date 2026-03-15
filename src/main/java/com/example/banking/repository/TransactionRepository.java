package com.example.banking.repository;

import com.example.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String> {
    @Query("SELECT t FROM Transaction t WHERE " +
            "t.sourceAccount.id = :accountId OR " +
            "t.destinationAccount.id = :accountId " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findAllByAccountId(@Param("accountId")String accountId);
}

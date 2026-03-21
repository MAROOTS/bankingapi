package com.example.banking.repository;

import com.example.banking.model.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, String> {
    List<FraudAlert> findByAccountIdOrderByCreatedAtDesc(String accountId);
    List<FraudAlert> findByStatus(FraudAlert.FraudStatus status);

    // Count recent transfers from an account within a time window
    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
            "t.sourceAccount.id = :accountId " +
            "AND t.type = 'TRANSFER' " +
            "AND t.createdAt >= :since")
    long countRecentTransfers(
            @Param("accountId") String accountId,
            @Param("since") LocalDateTime since);
}

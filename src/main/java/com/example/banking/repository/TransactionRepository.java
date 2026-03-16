package com.example.banking.repository;

import com.example.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String> {
    @Query("SELECT t FROM Transaction t WHERE " +
            "t.sourceAccount.id = :accountId OR " +
            "t.destinationAccount.id = :accountId " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findAllByAccountId(@Param("accountId")String accountId);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:minAmount IS NULL OR t.amount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR t.amount <= :maxAmount) " +
            "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findWithFilters( @Param("accountId") String accountId,
                                       @Param("type") Transaction.TransactionType type,
                                       @Param("status") Transaction.TransactionStatus status,
                                       @Param("minAmount") BigDecimal minAmount,
                                       @Param("maxAmount") BigDecimal maxAmount,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
            "t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId")
    long countByAccountId(@Param("accountId") String accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
            "t.destinationAccount.id = :accountId AND t.type = 'DEPOSIT'")
    BigDecimal sumDeposits(@Param("accountId") String accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
            "t.sourceAccount.id = :accountId AND t.type = 'WITHDRAWAL'")
    BigDecimal sumWithdrawals(@Param("accountId") String accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
            "t.sourceAccount.id = :accountId AND t.type = 'TRANSFER'")
    BigDecimal sumTransfersOut(@Param("accountId") String accountId);
}

package com.example.banking.service;

import com.example.banking.dto.FraudAlertResponse;
import com.example.banking.model.BankAccount;
import com.example.banking.model.FraudAlert;
import com.example.banking.model.Transaction;
import com.example.banking.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {
    private final FraudAlertRepository fraudAlertRepository;
    private final EmailService emailService;
    private static final BigDecimal LARGE_TRANSACTION_THRESHOLD = new BigDecimal("10000.00");
    private static final int MAX_TRANSFERS_PER_HOUR = 3;
    private static final int LATE_NIGHT_START = 0;  // midnight
    private static final int LATE_NIGHT_END = 4;    // 4am
    private static final BigDecimal BALANCE_DRAIN_THRESHOLD
            = new BigDecimal("0.80");
    public void analyze(Transaction transaction, BankAccount account){
        checkLargeTransaction(transaction,account);
        checkLateNightTransaction(transaction,account);
        checkRapidBalanceDrain(transaction,account);

        if (transaction.getType() == Transaction.TransactionType.TRANSFER){
            checkMultipleRapidTransfers(transaction,account);
        }
    }

    //Rule 1: Large transaction
    private void checkLargeTransaction(Transaction transaction, BankAccount account){
        if (transaction.getAmount()
                .compareTo(LARGE_TRANSACTION_THRESHOLD) > 0){
            raiseAlert(transaction, account, FraudAlert.FraudReason.LARGE_TRANSACTION,
                    "Transaction of " + transaction.getAmount() +
                    " exceeds threshold of " + LARGE_TRANSACTION_THRESHOLD);
        }
    }

    //Rule 2: Late night transaction

    private void checkLateNightTransaction(Transaction transaction, BankAccount account){
        int hour = LocalDateTime.now().getHour();
        if (hour < LATE_NIGHT_END){
            raiseAlert(transaction,account, FraudAlert.FraudReason.UNUSUAL_LATE_NIGHT,
                    "Transaction performed at unusual hour: " + hour + ":00"
            );
        }
    }

    //Rule 3: Rapid balance drain
    private void checkRapidBalanceDrain(Transaction transaction, BankAccount account){
        BigDecimal balanceBefore = account.getBalance()
                .add(transaction.getAmount());
        if (balanceBefore.compareTo(BigDecimal.ZERO)<= 0) return;

        BigDecimal drainRatio = transaction.getAmount()
                .divide(balanceBefore, 4, RoundingMode.HALF_UP);

        if (drainRatio.compareTo(BALANCE_DRAIN_THRESHOLD)>=0){
            raiseAlert(
                    transaction, account,
                    FraudAlert.FraudReason.RAPID_BALANCE_DRAIN,
                    "Transaction drained " +
                            drainRatio.multiply(BigDecimal.valueOf(100))
                                    .setScale(1, java.math.RoundingMode.HALF_UP) +
                            "% of account balance"
            );
        }
    }

    //Rule 4: Multiple rapid transfers
    private void checkMultipleRapidTransfers(Transaction transaction, BankAccount account){
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentTransfers = fraudAlertRepository.countRecentTransfers(account.getId(), oneHourAgo);
        if (recentTransfers>= MAX_TRANSFERS_PER_HOUR){
            raiseAlert(
                    transaction, account,
                    FraudAlert.FraudReason.MULTIPLE_RAPID_TRANSFERS,
                    recentTransfers + " transfers detected within the last hour"
            );
        }
    }

    private void raiseAlert(Transaction transaction, BankAccount account, FraudAlert.FraudReason reason, String details){
        FraudAlert alert = FraudAlert.builder()
                .transaction(transaction)
                .account(account)
                .reason(reason)
                .details(details)
                .status(FraudAlert.FraudStatus.OPEN)
                .build();
        fraudAlertRepository.save(alert);
        emailService.sendFraudAlertEmail(
                account.getOwner().getEmail(),
                account.getOwner().getFullName(),
                reason.name(),
                account.getAccountNumber(),
                details
        );
        transaction.setStatus(Transaction.TransactionStatus.FLAGGED);
        log.warn("Fraud alert raised: {} - {} - {}",
                reason, account.getAccountNumber(), details);
    }

    public List<FraudAlertResponse> getAlertsByAccount(String accountId) {
        return fraudAlertRepository
                .findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(FraudAlertResponse::from)
                .collect(Collectors.toList());
    }

    public List<FraudAlertResponse> getOpenAlerts() {
        return fraudAlertRepository
                .findByStatus(FraudAlert.FraudStatus.OPEN)
                .stream()
                .map(FraudAlertResponse::from)
                .collect(Collectors.toList());
    }

    public FraudAlertResponse updateAlertStatus(String alertId,
                                                FraudAlert.FraudStatus status) {
        FraudAlert alert = fraudAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setStatus(status);
        fraudAlertRepository.save(alert);
        return FraudAlertResponse.from(alert);
    }
}

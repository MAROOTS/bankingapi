package com.example.banking.service;

import com.example.banking.dto.InterestConfigResponse;
import com.example.banking.model.BankAccount;
import com.example.banking.model.InterestConfig;
import com.example.banking.model.Transaction;
import com.example.banking.repository.BankAccountRepository;
import com.example.banking.repository.InterestConfigRepository;
import com.example.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {
    private final BankAccountRepository accountRepository;
    private final InterestConfigRepository interestConfigRepository;
    private final TransactionRepository transactionRepository;

    public void applyMonthlyInterest(){
        log.info("Starting monthly interest calculation...");

        List<InterestConfig> activeConfigs = interestConfigRepository.findByActiveTrue();

        for (InterestConfig config : activeConfigs){
            List<BankAccount> accounts = accountRepository.findByAccountTypeAndStatus(config.getAccountType(),BankAccount.AccountStatus.ACTIVE);

            for (BankAccount account : accounts){
                applyInterestToAccount(account,config);
            }
            log.info("✅ Applied {}% interest to {} {} accounts",
                    config.getAnnualInterestRate(),
                    accounts.size(),
                    config.getAccountType());

        }
        log.info("🏁 Monthly interest calculation complete.");
    }
    private void applyInterestToAccount(BankAccount account, InterestConfig config){
        BigDecimal monthlyRate = config.getAnnualInterestRate()
                .divide(BigDecimal.valueOf(1200),6, RoundingMode.HALF_UP);

        BigDecimal interestAmount = account.getBalance()
                .multiply(monthlyRate)
                .setScale(2,RoundingMode.HALF_UP);

        if (interestAmount.compareTo(BigDecimal.ZERO)<=0) return;
        account.setBalance(account.getBalance().add(interestAmount));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .type(Transaction.TransactionType.DEPOSIT)
                .amount(interestAmount)
                .balanceAfter(account.getBalance())
                .destinationAccount(account)
                .description("Monthly interest payment - " +
                        config.getAnnualInterestRate() + "% p.a.")
                .status(Transaction.TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(transaction);
    }
    @Transactional
    public String triggerInterestManually() {
        applyMonthlyInterest();
        return "Interest applied successfully";
    }
    public List<InterestConfigResponse> getAllConfigs() {
        return interestConfigRepository.findAll()
                .stream()
                .map(InterestConfigResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public InterestConfigResponse setInterestRate(
            BankAccount.AccountType accountType,
            BigDecimal rate) {

        InterestConfig config = interestConfigRepository
                .findByAccountType(accountType)
                .orElse(InterestConfig.builder()
                        .accountType(accountType)
                        .active(true)
                        .build());

        config.setAnnualInterestRate(rate);
        config.setActive(true);
        interestConfigRepository.save(config);

        return InterestConfigResponse.from(config);
    }
}

package com.example.banking.service;

import com.example.banking.dto.*;
import com.example.banking.model.BankAccount;
import com.example.banking.model.Transaction;
import com.example.banking.repository.BankAccountRepository;
import com.example.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final FraudDetectionService fraudDetectionService;
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository accountRepository;
    private final EmailService emailService;

    @Transactional
    public TransactionResponse deposit(String email, DepositWithdrawRequest request){
        BankAccount account = getAccountByNumber(request.getAccountNumber());
        validateOwnership(account,email);
        validateAccountActive(account);

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .type(Transaction.TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .balanceAfter(account.getBalance())
                .destinationAccount(account)
                .description(request.getDescription() !=null ?
                        request.getDescription() : "Deposit")
                .build();

        Transaction saved = transactionRepository.save(transaction);
        fraudDetectionService.analyze(saved, account);
        emailService.sendTransactionEmail(
                saved.getDestinationAccount().getOwner().getEmail(),
                saved.getDestinationAccount().getOwner().getFullName(),
                "DEPOSIT",
                saved.getAmount(),
                saved.getDestinationAccount().getAccountNumber(),
                saved.getBalanceAfter(),
                saved.getDescription()
        );
        return TransactionResponse.from(saved);
    }

    @Transactional
    public TransactionResponse withdraw(String email,DepositWithdrawRequest request){
        BankAccount account = getAccountByNumber(request.getAccountNumber());
        validateOwnership(account,email);
        validateAccountActive(account);
        validateSufficientFunds(account,request.getAmount());

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .type(Transaction.TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .balanceAfter(account.getBalance())
                .sourceAccount(account)
                .description(request.getDescription() !=null ?
                        request.getDescription() : "withdraw")
                .build();
        Transaction saved = transactionRepository.save(transaction);
        fraudDetectionService.analyze(saved, account);
        emailService.sendTransactionEmail(
                saved.getSourceAccount().getOwner().getEmail(),
                saved.getSourceAccount().getOwner().getFullName(),
                "WITHDRAWAL",
                saved.getAmount(),
                saved.getSourceAccount().getAccountNumber(),
                saved.getBalanceAfter(),
                saved.getDescription()
        );
        return TransactionResponse.from(saved);
    }

    public TransactionResponse transfer(String email, TransferRequest request){
        if (request.getSourceAccountNumber()
                .equals(request.getDestinationAccountNumber())
        ){
            throw new RuntimeException("Cannot transfer to the same account");
        }
        BankAccount source = getAccountByNumber(request.getSourceAccountNumber());
        BankAccount destination = getAccountByNumber(request.getDestinationAccountNumber());
        validateOwnership(source,email);
        validateAccountActive(source);
        validateAccountActive(destination);
        validateSufficientFunds(source,request.getAmount());

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        destination.setBalance(destination.getBalance().add(request.getAmount()));

        accountRepository.save(source);
        accountRepository.save(destination);

        Transaction transaction = Transaction.builder()
                .type(Transaction.TransactionType.TRANSFER)
                .amount(request.getAmount())
                .balanceAfter(source.getBalance())
                .sourceAccount(source)
                .destinationAccount(destination)
                .description(request.getDescription() != null ?
                        request.getDescription() : "Transfer")
                .build();
        Transaction saved = transactionRepository.save(transaction);
        fraudDetectionService.analyze(saved, source);
        emailService.sendTransactionEmail(
                saved.getSourceAccount().getOwner().getEmail(),
                saved.getSourceAccount().getOwner().getFullName(),
                "TRANSFER",
                saved.getAmount(),
                saved.getSourceAccount().getAccountNumber(),
                saved.getBalanceAfter(),
                saved.getDescription()
        );
        return TransactionResponse.from(saved);
    }

    public List<TransactionResponse> getAccountTransactions(String accountId,String email){
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(()->new RuntimeException("Account not found"));
        validateOwnership(account,email);
        return transactionRepository.findAllByAccountId(accountId)
                .stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }
    public List<TransactionResponse> getFilteredTransactions(String accountId, String email, TransactionFilterRequest filter){
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(()->new RuntimeException("Account not found"));

        validateOwnership(account,email);

        List<Transaction> transactions = transactionRepository.findWithFilters(
                accountId,
                filter.getType(),
                filter.getStatus(),
                filter.getMinAmount(),
                filter.getMaxAmount(),
                filter.getStartDate(),
                filter.getEndDate()
        );
        Comparator<Transaction> comparator = switch (filter.getSortBy()){
            case "amount" -> Comparator.comparing(Transaction::getAmount);
            default -> Comparator.comparing(Transaction::getCreatedAt);
        };
        if ("ASC".equalsIgnoreCase(filter.getSortDirection())){
            transactions.sort(comparator);
        }else {
            transactions.sort(comparator.reversed());
        }
        return transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public TransactionSummaryResponse getAccountSummary(String accountId,String email){
        BankAccount account =accountRepository.findById(accountId)
                .orElseThrow(()->new RuntimeException("Account not found"));
        validateOwnership(account,email);

        return TransactionSummaryResponse.builder()
                .accountNumber(account.getAccountNumber())
                .currentBalance(account.getBalance())
                .totalTransactions(transactionRepository.countByAccountId(accountId))
                .totalDeposits(transactionRepository.sumDeposits(accountId))
                .totalWithdrawals(transactionRepository.sumWithdrawals(accountId))
                .totalTransfersOut(transactionRepository.sumTransfersOut(accountId))
                .build();
    }

//helpers
    private BankAccount getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + accountNumber));
    }

    private void validateOwnership(BankAccount account, String email) {
        if (!account.getOwner().getEmail().equals(email)) {
            throw new RuntimeException("Access denied");
        }
    }

    private void validateAccountActive(BankAccount account) {
        if (account.getStatus() != BankAccount.AccountStatus.ACTIVE) {
            throw new RuntimeException(
                    "Account " + account.getAccountNumber() + " is not active");
        }
    }

    private void validateSufficientFunds(BankAccount account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
    }
}

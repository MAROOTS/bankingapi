package com.example.banking.service;

import com.example.banking.dto.DepositWithdrawRequest;
import com.example.banking.dto.TransactionResponse;
import com.example.banking.dto.TransferRequest;
import com.example.banking.model.BankAccount;
import com.example.banking.model.Transaction;
import com.example.banking.repository.BankAccountRepository;
import com.example.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository accountRepository;

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
        return TransactionResponse.from(transactionRepository.save(transaction));
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
        return TransactionResponse.from(transactionRepository.save(transaction));
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
        return TransactionResponse.from(transactionRepository.save(transaction));
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

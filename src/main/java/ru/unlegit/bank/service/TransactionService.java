package ru.unlegit.bank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.entity.Account;
import ru.unlegit.bank.entity.Transaction;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.repository.TransactionRepository;

import java.util.List;

@Service
@AllArgsConstructor
public final class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction createTransaction(
            User user, Account sourceAccount, Account targetAccount,
            Transaction.Operation operation, double sum, String title, String details
    ) {
        return Transaction.builder()
                .user(user)
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .operation(operation)
                .sum(sum)
                .title(title)
                .details(details)
                .build();

    }

    public void log(
            User user, Account sourceAccount, Account targetAccount,
            Transaction.Operation operation, double sum, String title, String details
    ) {
        transactionRepository.save(
                createTransaction(user, sourceAccount, targetAccount, operation, sum, title, details)
        );
    }

    public void logAll(List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
    }
}
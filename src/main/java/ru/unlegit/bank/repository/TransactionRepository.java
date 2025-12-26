package ru.unlegit.bank.repository;

import org.springframework.data.repository.CrudRepository;
import ru.unlegit.bank.entity.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, String> {}
package ru.unlegit.bank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.repository.AccountRepository;

@Service
@AllArgsConstructor
public final class AccountService {

    private final AccountRepository accountRepository;

    public int countAccounts(User owner) {
        return accountRepository.countByOwner(owner);
    }
}
package ru.unlegit.bank.repository;

import org.springframework.data.repository.CrudRepository;
import ru.unlegit.bank.entity.Account;
import ru.unlegit.bank.entity.User;

public interface AccountRepository extends CrudRepository<Account, String> {

    int countByOwner(User owner);
}
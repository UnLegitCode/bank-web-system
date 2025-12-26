package ru.unlegit.bank.repository;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import ru.unlegit.bank.entity.DepositTerms;

import java.util.List;

public interface DepositTermsRepository extends CrudRepository<DepositTerms, String> {

    @NonNull
    @Override
    List<DepositTerms> findAll();
}
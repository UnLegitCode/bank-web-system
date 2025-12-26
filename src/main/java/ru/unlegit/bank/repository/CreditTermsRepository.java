package ru.unlegit.bank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.unlegit.bank.entity.CreditTerms;

import java.util.List;

public interface CreditTermsRepository extends CrudRepository<CreditTerms, String> {

    @NonNull
    @Override
    List<CreditTerms> findAll();
}
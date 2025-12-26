package ru.unlegit.bank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.unlegit.bank.entity.CashMachine;

import java.util.List;

public interface CashMachineRepository extends CrudRepository<CashMachine, String> {

    @NonNull
    @Override
    List<CashMachine> findAll();
}
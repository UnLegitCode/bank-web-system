package ru.unlegit.bank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.unlegit.bank.entity.Branch;

import java.util.List;

public interface BranchRepository extends CrudRepository<Branch, String> {

    @NonNull
    @Override
    List<Branch> findAll();
}
package ru.unlegit.bank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.entity.Branch;
import ru.unlegit.bank.repository.BranchRepository;

import java.util.List;

@Service
@AllArgsConstructor
public final class BranchService {

    private final BranchRepository branchRepository;

    public List<Branch> listBranches() {
        return branchRepository.findAll();
    }
}
package ru.unlegit.bank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.entity.DepositTerms;
import ru.unlegit.bank.repository.DepositTermsRepository;

import java.util.List;

@Service
@AllArgsConstructor
public final class DepositTermsService {

    private final DepositTermsRepository depositTermsRepository;

    public List<DepositTerms> listDepositTerms() {
        return depositTermsRepository.findAll();
    }

    public DepositTerms getDepositTerms(String id) {
        return depositTermsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Условия вклада не найдены"));
    }
}
package ru.unlegit.bank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.entity.CreditTerms;
import ru.unlegit.bank.repository.CreditTermsRepository;

import java.util.List;

@Service
@AllArgsConstructor
public final class CreditTermsService {

    private final CreditTermsRepository creditTermsRepository;

    public List<CreditTerms> listCreditTerms() {
        return creditTermsRepository.findAll();
    }

    public CreditTerms getCreditTerms(String id) {
        return creditTermsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Условия кредита не найдены"));
    }
}
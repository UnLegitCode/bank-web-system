package ru.unlegit.bank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.entity.CashMachine;
import ru.unlegit.bank.repository.CashMachineRepository;

import java.util.List;

@Service
@AllArgsConstructor
public final class CashMachineService {

    private final CashMachineRepository cashMachineRepository;

    public List<CashMachine> listCashMachines() {
        return cashMachineRepository.findAll();
    }
}
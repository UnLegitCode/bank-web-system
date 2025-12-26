package ru.unlegit.bank.controller.view;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.unlegit.bank.service.CashMachineService;

@Controller
@AllArgsConstructor
@RequestMapping("/cash-machine")
public final class CashMachineController {

    private final CashMachineService cashMachineService;

    @GetMapping
    public String listCashMachines(Model model) {
        model.addAttribute("cash_machines", cashMachineService.listCashMachines());

        return "cash-machines/list";
    }
}
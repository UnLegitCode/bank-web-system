package ru.unlegit.bank.controller.view;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.unlegit.bank.service.BranchService;

@Controller
@AllArgsConstructor
@RequestMapping("/branch")
public final class BranchController {

    private final BranchService branchService;

    @GetMapping
    public String listBranches(Model model) {
        model.addAttribute("branches", branchService.listBranches());

        return "branches/list";
    }
}
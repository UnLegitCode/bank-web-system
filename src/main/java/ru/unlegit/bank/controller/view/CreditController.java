package ru.unlegit.bank.controller.view;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.exception.UserNotFoundException;
import ru.unlegit.bank.service.CreditService;
import ru.unlegit.bank.service.UserService;
import ru.unlegit.bank.util.MiscUtil;

@Controller
@AllArgsConstructor
@RequestMapping("/credit")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CreditController {

    CreditService creditService;
    UserService userService;

    @PostMapping("/open")
    public String openCredit(
            @RequestParam String termsId,
            @RequestParam String cardId,
            @RequestParam @Min(1) double amount,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            creditService.openCredit(user, termsId, cardId, amount);
            redirectAttrs.addFlashAttribute("success", "Кредит успешно открыт!");

            return "redirect:/home";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());

            return "redirect:/credit/terms";
        } catch (Exception exception) {
            redirectAttrs.addFlashAttribute("error", "Ошибка при открытии кредита");

            return "redirect:/credit/terms";
        }
    }

    @PostMapping("/{creditId}/repay")
    public String repayCredit(
            @PathVariable String creditId,
            @RequestParam double amount,
            @RequestParam String cardId,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User currentUser = userService.getUserFromAuthentication(authentication);

        try {
            if (creditService.repayCredit(currentUser, creditId, cardId, amount)) {
                redirectAttrs.addFlashAttribute("success", "Поздравляем! Кредит полностью погашен.");
            } else {
                redirectAttrs.addFlashAttribute(
                        "success", "Кредит погашен на " + MiscUtil.formatBalance(amount)
                );
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }
}
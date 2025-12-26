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
import ru.unlegit.bank.service.DepositService;
import ru.unlegit.bank.service.UserService;
import ru.unlegit.bank.util.MiscUtil;

@Controller
@AllArgsConstructor
@RequestMapping("/deposit")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class DepositController {

    DepositService depositService;
    UserService userService;

    @PostMapping("/open")
    public String openDeposit(
            @RequestParam String termsId,
            @RequestParam String cardId,
            @RequestParam @Min(1) double amount,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            depositService.openDeposit(user, termsId, cardId, amount);
            redirectAttrs.addFlashAttribute("success", "Вклад успешно открыт!");

            return "redirect:/home";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());

            return "redirect:/deposit/terms";
        } catch (Exception exception) {
            redirectAttrs.addFlashAttribute("error", "Ошибка при открытии вклада");

            return "redirect:/deposit/terms";
        }
    }

    @PostMapping("/{depositId}/replenish")
    public String replenishDeposit(
            @PathVariable String depositId,
            @RequestParam double amount,
            @RequestParam String cardId,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User currentUser = userService.getUserFromAuthentication(authentication);

        try {
            depositService.replenish(currentUser, depositId, cardId, amount);
            redirectAttrs.addFlashAttribute(
                    "success", "Вклад пополнен на " + MiscUtil.formatBalance(amount)
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }

    @PostMapping("/{depositId}/withdraw")
    public String withdrawFromDeposit(
            @PathVariable String depositId,
            @RequestParam double amount,
            @RequestParam String cardId,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User currentUser = userService.getUserFromAuthentication(authentication);

        try {
            depositService.withdraw(currentUser, depositId, cardId, amount);
            redirectAttrs.addFlashAttribute(
                    "success", "С вклада снято " + MiscUtil.formatBalance(amount)
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }

    @PostMapping("/{depositId}/close")
    public String withdrawFromDeposit(
            @PathVariable String depositId,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User currentUser = userService.getUserFromAuthentication(authentication);

        try {
            depositService.close(currentUser, depositId);
            redirectAttrs.addFlashAttribute("success", "Вклад закрыт");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }
}
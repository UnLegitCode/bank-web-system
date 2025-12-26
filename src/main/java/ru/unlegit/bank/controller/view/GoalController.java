package ru.unlegit.bank.controller.view;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.exception.UserNotFoundException;
import ru.unlegit.bank.service.GoalService;
import ru.unlegit.bank.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDate;

@Controller
@AllArgsConstructor
@RequestMapping("/goal")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GoalController {

    GoalService goalService;
    UserService userService;

    @PostMapping("/create")
    public String createGoal(
            @RequestParam String displayName,
            @RequestParam double targetBalance,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            goalService.openGoal(user, targetBalance, displayName, Timestamp.valueOf(targetDate.atStartOfDay()));
            redirectAttrs.addFlashAttribute("success", "Цель успешно создана");
        } catch (IllegalArgumentException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }

    @PostMapping("/{goalId}/replenish")
    public String replenishGoal(
            @PathVariable String goalId,
            @RequestParam double amount,
            @RequestParam String cardId,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            goalService.replenishGoal(user, goalId, cardId, amount);
            redirectAttrs.addFlashAttribute(
                    "success", "Цель успешно пополнена на " + String.format("%,.2f ₽", amount)
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }

    @PostMapping("/close")
    public String closeGoal(
            @RequestParam String goalId,
            @RequestParam String cardId,
            Authentication authentication,
            RedirectAttributes redirectAttrs
    ) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            goalService.closeGoal(user, goalId, cardId);
            redirectAttrs.addFlashAttribute(
                    "success", "Цель успешно закрыта. Средства зачислены на карту."
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }
}
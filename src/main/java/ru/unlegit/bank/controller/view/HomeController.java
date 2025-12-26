package ru.unlegit.bank.controller.view;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.exception.UserNotFoundException;
import ru.unlegit.bank.service.*;

@Controller
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class HomeController {

    UserService userService;
    AccountService accountService;
    CardService cardService;
    DepositService depositService;
    GoalService goalService;
    CreditService creditService;

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        model.addAttribute("user", user);
        model.addAttribute("cards", cardService.getUserCards(user));
        model.addAttribute("deposits", depositService.getUserDeposits(user));
        model.addAttribute("goals", goalService.getUserGoals(user));
        model.addAttribute("credits", creditService.getUserCredits(user));

        return "home";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        model.addAttribute("user", user);
        model.addAttribute("accountsCount", accountService.countAccounts(user));
        model.addAttribute("activeCardsCount", cardService.countActiveCards(user));

        return "profile";
    }
}
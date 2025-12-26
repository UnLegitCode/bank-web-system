package ru.unlegit.bank.controller.view;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.exception.UserNotFoundException;
import ru.unlegit.bank.service.CardService;
import ru.unlegit.bank.service.DepositTermsService;
import ru.unlegit.bank.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/deposit/terms")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class DepositTermsController {

    DepositTermsService depositTermsService;
    CardService cardService;
    UserService userService;

    @GetMapping
    public String list(Model model, Authentication authentication) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        model.addAttribute("terms", depositTermsService.listDepositTerms());
        model.addAttribute("cards", cardService.getUserCards(user));

        return "deposits/terms/list";
    }
}
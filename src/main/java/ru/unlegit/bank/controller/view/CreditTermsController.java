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
import ru.unlegit.bank.service.CreditTermsService;
import ru.unlegit.bank.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/credit/terms")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreditTermsController {

    CreditTermsService creditTermsService;
    CardService cardService;
    UserService userService;

    @GetMapping
    public String list(Model model, Authentication authentication) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        model.addAttribute("terms", creditTermsService.listCreditTerms());
        model.addAttribute("cards", cardService.getUserCards(user));

        return "credit/terms/list";
    }
}
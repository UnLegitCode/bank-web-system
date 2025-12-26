package ru.unlegit.bank.controller.view;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unlegit.bank.dto.card.CardForm;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.exception.UserNotFoundException;
import ru.unlegit.bank.service.CardService;
import ru.unlegit.bank.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/card")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CardController {

    UserService userService;
    CardService cardService;

    @GetMapping("/issue")
    public String issueCard(Model model) {
        model.addAttribute("card_form", new CardForm());

        return "cards/issue";
    }

    @PostMapping("/issue")
    public String issueCard(
            @ModelAttribute("card_form") @Valid CardForm cardForm,
            RedirectAttributes redirectAttrs, Authentication authentication
    ) throws UserNotFoundException {
        User user = userService.getUserFromAuthentication(authentication);

        try {
            cardService.issueCard(user, cardForm);
            redirectAttrs.addFlashAttribute("success", "Карта успешно выпущена!");

            return "redirect:/home";
        } catch (Exception exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());

            return "cards/issue";
        }
    }

    @PostMapping("/{cardId}/change-pin")
    public String changePin(
            @PathVariable String cardId,
            @RequestParam String currentPin,
            @RequestParam String newPin,
            @RequestParam String newPinConfirm,
            Authentication auth,
            RedirectAttributes redirectAttrs
    ) {
        System.out.println("Card id: " + cardId);

        if (newPin.equals(newPinConfirm)) {
            try {
                cardService.changePin(cardId, auth.getName(), currentPin, newPin);
                redirectAttrs.addFlashAttribute("success", "ПИН-код успешно изменён!");
            } catch (Exception exception) {
                redirectAttrs.addFlashAttribute("error", exception.getMessage());
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "Новый ПИН не совпадает с подтверждением");
        }

        return "redirect:/home";
    }

    @PostMapping("/{id}/close")
    public String closeCard(@PathVariable String id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            cardService.closeCard(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Карта успешно закрыта");
        } catch (Exception exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }

    @PostMapping("/{id}/toggle-block")
    public String toggleBlock(@PathVariable String id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            cardService.toggleBlock(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Статус блокировки изменён");
        } catch (Exception exception) {
            redirectAttrs.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/home";
    }
}
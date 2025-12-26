package ru.unlegit.bank.controller.view;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.unlegit.bank.dto.auth.RegistrationStep1Data;
import ru.unlegit.bank.dto.auth.RegistrationStep2Data;
import ru.unlegit.bank.dto.auth.RegistrationStep3Data;
import ru.unlegit.bank.service.UserService;

@Controller
@AllArgsConstructor
public final class AuthenticationController {

    private static boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private final UserService userService;

    @GetMapping("/register")
    public String register() {
        return "redirect:/register/step1";
    }

    @GetMapping("/register/step1")
    public String showStep1(Authentication authentication, HttpSession session, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/home";
        }

        RegistrationStep1Data step1 = (RegistrationStep1Data) session.getAttribute("step1");

        if (step1 == null) {
            step1 = new RegistrationStep1Data();
        }

        model.addAttribute("step1", step1);

        return "auth/register/step1";
    }

    @PostMapping("/register/step1")
    public String processStep1(
            @ModelAttribute("step1") @Valid RegistrationStep1Data step1,
            BindingResult result, HttpSession session
    ) {
        if (result.hasErrors()) {
            return "auth/register/step1";
        }

        session.setAttribute("step1", step1);

        return "redirect:/register/step2";
    }

    @GetMapping("/register/step2")
    public String showStep2(HttpSession session, Model model) {
        if (session.getAttribute("step1") == null) {
            return "redirect:/register/step1";
        }

        RegistrationStep2Data step2 = (RegistrationStep2Data) session.getAttribute("step2");

        if (step2 == null) {
            step2 = new RegistrationStep2Data();
        }

        model.addAttribute("step2", step2);

        return "auth/register/step2";
    }

    @PostMapping("/register/step2")
    public String processStep2(
            @ModelAttribute("step2") @Valid RegistrationStep2Data step2,
            BindingResult result, HttpSession session
    ) {
        if (session.getAttribute("step1") == null) {
            return "redirect:/register/step1";
        }

        if (result.hasErrors()) {
            return "auth/register/step2";
        }

        session.setAttribute("step2", step2);

        return "redirect:/register/step3";
    }

    @GetMapping("/register/step3")
    public String showStep3(HttpSession session, Model model) {
        if (session.getAttribute("step1") == null) {
            return "redirect:/register/step1";
        }

        if (session.getAttribute("step2") == null) {
            return "redirect:/register/step2";
        }

        RegistrationStep3Data step3 = (RegistrationStep3Data) session.getAttribute("step3");

        if (step3 == null) {
            step3 = new RegistrationStep3Data();
        }

        model.addAttribute("step3", step3);

        return "auth/register/step3";
    }

    @PostMapping("/register/step3")
    public String processStep3(
            @ModelAttribute("step3") @Valid RegistrationStep3Data step3,
            BindingResult result, HttpSession session
    ) {
        if (session.getAttribute("step1") == null) {
            return "redirect:/register/step1";
        }

        if (session.getAttribute("step2") == null) {
            return "redirect:/register/step2";
        }

        if (result.hasErrors()) {
            return "auth/register/step3";
        }

        if (!step3.getPassword().equals(step3.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.step3", "Пароли не совпадают");

            return "auth/register/step3";
        }

        session.setAttribute("step3", step3);

        return "redirect:/register/confirm";
    }

    @GetMapping("/register/confirm")
    public String showConfirmation(HttpSession session, Model model) {
        RegistrationStep1Data step1 = (RegistrationStep1Data) session.getAttribute("step1");
        RegistrationStep2Data step2 = (RegistrationStep2Data) session.getAttribute("step2");
        RegistrationStep3Data step3 = (RegistrationStep3Data) session.getAttribute("step3");

        if (step1 == null || step2 == null || step3 == null) {
            return "redirect:/register/step1";
        }

        model.addAttribute("step1", step1);
        model.addAttribute("step2", step2);
        model.addAttribute("step3", step3);

        return "auth/register/confirm";
    }

    @PostMapping("/register/complete")
    public String completeRegistration(HttpSession session, RedirectAttributes redirectAttributes) {
        RegistrationStep1Data step1 = (RegistrationStep1Data) session.getAttribute("step1");
        RegistrationStep2Data step2 = (RegistrationStep2Data) session.getAttribute("step2");
        RegistrationStep3Data step3 = (RegistrationStep3Data) session.getAttribute("step3");

        if (step1 == null || step2 == null || step3 == null) {
            redirectAttributes.addFlashAttribute("error", "Не все данные заполнены");
            return "redirect:/register/step1";
        }

        try {
            userService.createUser(step1, step2, step3);

            session.removeAttribute("step1");
            session.removeAttribute("step2");
            session.removeAttribute("step3");

            redirectAttributes.addFlashAttribute("success",
                    "Регистрация прошла успешно! Теперь вы можете войти в систему.");
            return "redirect:/login";

        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "Ошибка регистрации: " + exception.getMessage());

            return "redirect:/register/step1";
        }
    }

    @GetMapping("/register/back-to-step1")
    public String backToStep1() {
        return "redirect:/register/step1";
    }

    @GetMapping("/register/back-to-step2")
    public String backToStep2(HttpSession session) {
        if (session.getAttribute("step1") == null) {
            return "redirect:/register/step1";
        }

        return "redirect:/register/step2";
    }

    @GetMapping(value = {"/login"})
    public String login(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/home";
        }

        return "auth/login";
    }
}
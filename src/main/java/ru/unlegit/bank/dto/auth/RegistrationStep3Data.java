package ru.unlegit.bank.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class RegistrationStep3Data {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Введите корректный email")
    String email;
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    String password;
    @NotBlank(message = "Подтверждение пароля обязательно")
    String confirmPassword;
}
package ru.unlegit.bank.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationStep1Data {

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;
    @NotBlank(message = "Имя обязательно")
    private String firstName;
    String middleName;
    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    LocalDate birthDate;
    @NotBlank(message = "Регион обязателен")
    String region;
    @NotBlank(message = "Город обязателен")
    String city;
}
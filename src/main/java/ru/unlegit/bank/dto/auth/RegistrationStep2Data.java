package ru.unlegit.bank.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class RegistrationStep2Data {

    @NotBlank(message = "Серия паспорта обязательна")
    @Pattern(regexp = "\\d{4}", message = "Серия паспорта должна содержать 4 цифры")
    String passportSeries;
    @NotBlank(message = "Номер паспорта обязателен")
    @Pattern(regexp = "\\d{6}", message = "Номер паспорта должен содержать 6 цифр")
    String passportNumber;
    @NotNull(message = "Дата выдачи обязательна")
    @Past(message = "Дата выдачи должна быть в прошлом")
    LocalDate issueDate;
}
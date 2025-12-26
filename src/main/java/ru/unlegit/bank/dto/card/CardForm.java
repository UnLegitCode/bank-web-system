package ru.unlegit.bank.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class CardForm {

    @NotBlank(message = "Выберите тип карты")
    String cardType;
    @NotBlank(message = "Выберите валюту")
    String currency;
    @Pattern(regexp = "\\d{4}", message = "ПИН-код должен содержать 4 цифры")
    String pin;
}
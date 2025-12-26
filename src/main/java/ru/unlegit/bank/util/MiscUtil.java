package ru.unlegit.bank.util;

import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.text.DecimalFormat;

@UtilityClass
public class MiscUtil {

    private final DecimalFormat BALANCE_FORMAT = new DecimalFormat("###,###.##");

    public String formatBalance(double balance) {
        return BALANCE_FORMAT.format(balance);
    }

    @SuppressWarnings("deprecation")
    public String formatDate(Timestamp date) {
        int day = date.getDate();
        int month = date.getMonth() + 1;
        int year = date.getYear() + 1900;

        StringBuilder builder = new StringBuilder();

        if (day < 10) {
            builder.append('0');
        }

        builder.append(day).append('.');

        if (month < 10) {
            builder.append('0');
        }

        return builder.append(month).append('.').append(year).toString();
    }

    public static int calculateCheckDigit(String partialNumber) {
        int sum = 0;
        boolean alternate = false;

        // Идём с конца частичного номера
        for (int i = partialNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(partialNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            sum += digit;
            alternate = !alternate;
        }

        return (10 - (sum % 10)) % 10;
    }

    public static String formatCardNumber(String cardNumber) {
        cardNumber = cardNumber.replaceAll("\\s+", "");

        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }

            formatted.append(cardNumber.charAt(i));
        }

        return formatted.toString();
    }
}
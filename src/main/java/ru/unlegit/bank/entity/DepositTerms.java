package ru.unlegit.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.unlegit.bank.util.MiscUtil;

import java.util.concurrent.TimeUnit;

@Entity
@Getter
@Setter
@Table(name = "deposit_terms")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class DepositTerms extends BaseEntity {

    @Column(nullable = false, length = 32)
    String name;
    @Column(nullable = false)
    String description;
    @Column(name = "min_sum", nullable = false)
    int minSum;
    @Column(name = "interest_rate", nullable = false)
    int interestRate;
    @Column(name = "term_months", nullable = false)
    int termMonths;
    @Column(name = "capitalization_period", nullable = false)
    CapitalizationPeriod capitalizationPeriod;
    @Column(nullable = false)
    boolean replenishable;
    @Column(name = "partial_withdrawal", nullable = false)
    boolean partialWithdrawal;

    public String formatMinSum() {
        return MiscUtil.formatBalance(minSum);
    }

    @Getter
    @FieldDefaults(level =  AccessLevel.PRIVATE, makeFinal = true)
    public enum CapitalizationPeriod {

        MONTH(1, "Ежемесячно"),
        THREE_MONTHS(3, "Раз в 3 месяца"),
        SIX_MONTHS(6, "Раз в 6 месяцев"),
        YEAR(12, "Раз в год"),
        TWO_YEAR(24, "Раз в 2 года");

        int months;
        long millis;
        String displayName;

        CapitalizationPeriod(int months, String displayName) {
            this.months = months;
            millis = TimeUnit.DAYS.toMillis(months * 30L);
            this.displayName = displayName;
        }
    }
}
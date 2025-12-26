package ru.unlegit.bank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.unlegit.bank.util.MiscUtil;

@Entity
@Getter
@Setter
@Table(name = "credit_terms")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class CreditTerms extends BaseEntity {

    @Column(nullable = false, length = 32)
    String name;
    @Column(nullable = false)
    String description;
    @Column(name = "min_sum", nullable = false)
    double minSum;
    @Column(name = "max_sum", nullable = false)
    double maxSum;
    @Column(name = "interest_rate", nullable = false)
    int interestRate;

    public String formatMinSum() {
        return MiscUtil.formatBalance(minSum);
    }

    public String formatMaxSum() {
        return MiscUtil.formatBalance(maxSum);
    }
}
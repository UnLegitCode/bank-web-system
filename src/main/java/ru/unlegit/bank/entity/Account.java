package ru.unlegit.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.unlegit.bank.util.MiscUtil;

import java.util.concurrent.ThreadLocalRandom;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "accounts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Account extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    User owner;
    long number = ThreadLocalRandom.current().nextLong(100_000_000L, 1_000_000_000L);
    double balance;

    public Account(User owner) {
        this.owner = owner;
    }

    public String formatBalance() {
        return MiscUtil.formatBalance(balance) + "₽";
    }
}
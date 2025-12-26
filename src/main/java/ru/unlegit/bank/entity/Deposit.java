package ru.unlegit.bank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.unlegit.bank.util.MiscUtil;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "deposits")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Deposit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    User owner;
    @ManyToOne(optional = false)
    Account account;
    @ManyToOne(optional = false)
    DepositTerms terms;
    @Column(name = "initial_sum", nullable = false, updatable = false)
    double initialSum;
    @Column(name = "auto_renew", nullable = false)
    boolean autoRenew;
    @Column(nullable = false)
    boolean closed = false;
    @CreationTimestamp
    @Column(name = "opened_at", nullable = false, updatable = false)
    Timestamp openedAt;
    @Column(name = "next_capitalization", nullable = false)
    Timestamp nextCapitalization;

    public String formatOpenedAt() {
        return MiscUtil.formatDate(openedAt);
    }

    public void updateNextCapitalization() {
        nextCapitalization = Timestamp.from(Instant.now().plusMillis(terms.getCapitalizationPeriod().getMillis()));
    }
}
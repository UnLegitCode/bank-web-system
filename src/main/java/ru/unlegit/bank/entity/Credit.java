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
import java.util.concurrent.TimeUnit;

@Entity
@Getter
@Setter
@Table(name = "credits")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Credit extends BaseEntity {

    private static final long INTEREST_ACCRUAL_INTERVAL = TimeUnit.DAYS.toMillis(365);

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    User owner;
    @ManyToOne(optional = false)
    Account account;
    @Column(name = "interest_rate", nullable = false, updatable = false)
    int interestRate;
    @Column(name = "initial_sum", nullable = false)
    double initialSum;
    @Column(name = "target_balance", nullable = false)
    double targetBalance;
    @Column(name = "next_interest_accrual", nullable = false)
    Timestamp nextInterestAccrual;
    @Column(nullable = false)
    boolean closed = false;
    @CreationTimestamp
    @Column(name = "opened_at", nullable = false, updatable = false)
    Timestamp openedAt;

    public String formatInitialSum() {
        return MiscUtil.formatBalance(initialSum);
    }

    public String formatTargetBalance() {
        return MiscUtil.formatBalance(targetBalance);
    }

    public String formatNextInterestAccrual() {
        return MiscUtil.formatDate(nextInterestAccrual);
    }

    public String formatOpenedAt() {
        return MiscUtil.formatDate(openedAt);
    }

    public void chargeInterest() {
        targetBalance += (initialSum * interestRate / 100);
        updateNextInterestAccrual();
    }

    public void updateNextInterestAccrual() {
        nextInterestAccrual = Timestamp.from(Instant.now().plusMillis(INTEREST_ACCRUAL_INTERVAL));
    }

    public double leftToRepaid() {
        return targetBalance - account.getBalance();
    }

    public String formatLeftToRepaid() {
        return MiscUtil.formatBalance(leftToRepaid());
    }

    public boolean isRepaid() {
        return account.getBalance() >= targetBalance;
    }

    public double getRepaymentProgress() {
        return Math.min(100D, Math.max(0D, account.getBalance() / targetBalance * 100D));
    }

    public String getRepaymentProgressFormatted() {
        return String.format("%.1f", getRepaymentProgress());
    }
}
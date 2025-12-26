package ru.unlegit.bank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.unlegit.bank.util.MiscUtil;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "goals")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Goal extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    User owner;
    @OneToOne(optional = false)
    Account account;
    @Column(name = "target_balance", nullable = false)
    double targetBalance;
    @Column(name = "display_name",  nullable = false, updatable = false, length = 32)
    String displayName;
    @Column(name = "target_date", nullable = false, updatable = false)
    Timestamp targetDate;
    @Column(nullable = false)
    boolean closed = false;
    @CreationTimestamp
    @Column(name = "opened_at", nullable = false, updatable = false)
    Timestamp openedAt;

    public String formatTargetBalance() {
        return MiscUtil.formatBalance(targetBalance);
    }

    public int getProgress() {
        return (int) (account.getBalance() / targetBalance * 100);
    }

    public String formatTargetDate() {
        return MiscUtil.formatDate(targetDate);
    }

    public String formatOpenedAt() {
        return MiscUtil.formatDate(openedAt);
    }

    public boolean isCompleted() {
        return account.getBalance() >= targetBalance;
    }

    public double leftToComplete() {
        return targetBalance - account.getBalance();
    }
}
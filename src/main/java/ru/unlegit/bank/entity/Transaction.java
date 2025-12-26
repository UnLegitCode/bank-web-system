package ru.unlegit.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account", updatable = false)
    Account sourceAccount;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_account", updatable = false)
    Account targetAccount;
    @Column(nullable = false, updatable = false)
    Operation operation;
    @Column(nullable = false, updatable = false)
    double sum;
    @Column(nullable = false, updatable = false, length = 32)
    String title;
    @Column(nullable = false, updatable = false)
    String details;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Timestamp createdAt;

    public enum Operation {
        REPLENISHMENT,
        DEBITING,
        TRANSFER
    }
}
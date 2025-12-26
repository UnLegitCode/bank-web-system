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
@Table(name = "cards")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Card extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    User owner;
    @OneToOne(optional = false)
    Account account;
    @Column(nullable = false, updatable = false, unique = true, length = 19)
    String number;
    @Column(nullable = false)
    String pinHash;
    @Column(nullable = false)
    boolean blocked;
    @Column(nullable = false)
    boolean closed;
    @CreationTimestamp
    @Column(name = "opened_at", nullable = false, updatable = false)
    Timestamp openedAt;

    public String formatOpenedAt() {
        return MiscUtil.formatDate(openedAt);
    }
}
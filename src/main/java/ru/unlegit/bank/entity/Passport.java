package ru.unlegit.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.unlegit.bank.util.MiscUtil;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "passports", indexes = {
        @Index(name = "series_number_pair", columnList = "series, number", unique = true)
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Passport extends BaseEntity {

    @Column(nullable = false, updatable = false)
    String series;
    @Column(nullable = false, updatable = false)
    String number;
    @Column(name = "issue_date", nullable = false, updatable = false)
    Timestamp issueDate;

    public Passport(String series, String number, Timestamp issueDate) {
        this.series = series;
        this.number = number;
        this.issueDate = issueDate;
    }

    public String formatIssueDate() {
        return MiscUtil.formatDate(issueDate);
    }
}
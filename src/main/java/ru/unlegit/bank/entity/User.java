package ru.unlegit.bank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.unlegit.bank.dto.auth.RegistrationStep1Data;
import ru.unlegit.bank.dto.auth.RegistrationStep2Data;
import ru.unlegit.bank.util.MiscUtil;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class User extends BaseEntity {

    @Column(nullable = false)
    String lastName;
    @Column(nullable = false)
    String firstName;
    @Column(nullable = false)
    String patronymic;
    @Column(name = "birth_date", nullable = false, updatable = false)
    Timestamp birthDate;
    @Column(nullable = false)
    String region;
    @Column(nullable = false)
    String city;
    @OneToOne(cascade = CascadeType.ALL)
    Passport passport;
    @Email
    @Column(nullable = false, unique = true)
    String email;
    @Column(name = "password_hash", nullable = false)
    String passwordHash;
    @Column(nullable = false)
    Role role = Role.USER;
    @CreationTimestamp
    @Column(name = "registered_at", nullable = false, updatable = false)
    Timestamp registeredAt;

    public User(
            RegistrationStep1Data step1, RegistrationStep2Data step2, String email, String passwordHash
    ) {
        lastName = step1.getLastName();
        firstName = step1.getFirstName();
        patronymic = step1.getMiddleName();
        birthDate = Timestamp.valueOf(step1.getBirthDate().atStartOfDay());
        region = step1.getRegion();
        city = step1.getCity();

        passport = new Passport(
                step2.getPassportSeries(), step2.getPassportNumber(),
                Timestamp.valueOf(step2.getIssueDate().atStartOfDay())
        );

        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UserDetails buildDetails() {
        return new org.springframework.security.core.userdetails.User(
                email, passwordHash, List.of(role.getAuthority())
        );
    }

    public String formatBirthDate() {
        return MiscUtil.formatDate(birthDate);
    }

    public enum Role {

        USER,
        ADMIN;

        private GrantedAuthority getAuthority() {
            return new SimpleGrantedAuthority("ROLE_" + name());
        }
    }
}
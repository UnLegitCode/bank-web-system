package ru.unlegit.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "branches")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Branch extends BaseEntity {

    @OneToOne(optional = false)
    Address address;
    @Column(name = "working_hours", nullable = false, length = 64)
    String workingHours;
    @Column(name = "contact_phone_number", nullable = false, unique = true)
    String contactPhoneNumber;
}
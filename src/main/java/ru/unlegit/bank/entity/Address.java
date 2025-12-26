package ru.unlegit.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "addresses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Address extends BaseEntity {

    @Column(nullable = false)
    String region;
    @Column(nullable = false)
    String city;
    @Column(nullable = false)
    String street;
    @Column(name = "house_number", nullable = false)
    int houseNumber;
}
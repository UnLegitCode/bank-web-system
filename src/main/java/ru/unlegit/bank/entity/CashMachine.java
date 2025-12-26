package ru.unlegit.bank.entity;

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
@Table(name = "cash_machines")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class CashMachine extends BaseEntity {

    @OneToOne(optional = false)
    Address address;
}
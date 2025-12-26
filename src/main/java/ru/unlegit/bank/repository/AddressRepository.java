package ru.unlegit.bank.repository;

import org.springframework.data.repository.CrudRepository;
import ru.unlegit.bank.entity.Address;

public interface AddressRepository extends CrudRepository<Address, String> {}
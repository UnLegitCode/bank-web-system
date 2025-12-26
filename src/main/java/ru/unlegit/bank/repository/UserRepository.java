package ru.unlegit.bank.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import ru.unlegit.bank.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    @EntityGraph(attributePaths = {"passport"})
    Optional<User> findByEmail(String email);
}
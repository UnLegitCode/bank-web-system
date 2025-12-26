package ru.unlegit.bank.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.unlegit.bank.entity.Goal;
import ru.unlegit.bank.entity.User;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends CrudRepository<Goal, String> {

    @NonNull
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"account"})
    Optional<Goal> findById(@NonNull String id);

    @EntityGraph(attributePaths = {"account"})
    List<Goal> findAllByOwnerAndClosed(User owner, boolean closed);
}
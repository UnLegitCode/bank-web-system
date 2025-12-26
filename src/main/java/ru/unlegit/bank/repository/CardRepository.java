package ru.unlegit.bank.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.unlegit.bank.entity.Card;
import ru.unlegit.bank.entity.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends CrudRepository<Card, String> {

    @NonNull
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"account"})
    Optional<Card> findById(@NonNull String id);

    @EntityGraph(attributePaths = {"account"})
    List<Card> findAllByOwnerAndClosed(User owner, boolean closed);

    int countByOwnerAndBlockedAndClosed(User owner, boolean blocked, boolean closed);

    List<Card> findAllByOpenedAtBetween(Timestamp startDate, Timestamp endDate);
}
package ru.unlegit.bank.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.unlegit.bank.entity.Deposit;
import ru.unlegit.bank.entity.User;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DepositRepository extends CrudRepository<Deposit, String> {

    @NonNull
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"account"})
    Optional<Deposit> findById(@NonNull String id);

    @EntityGraph(attributePaths = {"account"})
    List<Deposit> findAllByOwnerAndClosed(User owner, boolean closed);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"account"})
    @Query("""
        SELECT deposit FROM Deposit deposit
        WHERE NOT deposit.closed AND FUNCTION('DATE', deposit.nextCapitalization) = :today
    """)
    List<Deposit> findActiveWithTodayCapitalization(@Param("today") LocalDate today);

    List<Deposit> findAllByOpenedAtBetween(Timestamp startDate, Timestamp endDate);
}
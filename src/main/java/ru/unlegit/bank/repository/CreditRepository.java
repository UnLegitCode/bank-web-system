package ru.unlegit.bank.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.unlegit.bank.entity.Credit;
import ru.unlegit.bank.entity.User;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CreditRepository extends CrudRepository<Credit, String> {

    @NonNull
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"account"})
    Optional<Credit> findById(@NonNull String id);

    @EntityGraph(attributePaths = {"account"})
    List<Credit> findAllByOwnerAndClosed(User owner, boolean closed);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"account"})
    @Query("""
        SELECT credit FROM Credit credit
        WHERE NOT credit.closed AND FUNCTION('DATE', credit.nextInterestAccrual) = :today
    """)
    List<Credit> findActiveWithTodayInterestAccrual(@Param("today") LocalDate today);

    List<Credit> findAllByOpenedAtBetween(Timestamp startDate, Timestamp endDate);
}
package ru.unlegit.bank.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.unlegit.bank.entity.*;
import ru.unlegit.bank.repository.AccountRepository;
import ru.unlegit.bank.repository.GoalRepository;
import ru.unlegit.bank.util.MiscUtil;

import java.sql.Timestamp;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoalService {

    GoalRepository goalRepository;
    CardService cardService;
    AccountRepository accountRepository;
    TransactionService transactionService;

    public List<Goal> getUserGoals(User user) {
        return goalRepository.findAllByOwnerAndClosed(user, false);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void openGoal(User user, double targetBalance, String displayName, Timestamp targetDate) {
        Account goalAccount = accountRepository.save(new Account(user));
        Goal goal = new Goal();

        goal.setOwner(user);
        goal.setAccount(goalAccount);
        goal.setTargetBalance(targetBalance);
        goal.setDisplayName(displayName);
        goal.setTargetDate(targetDate);

        goalRepository.save(goal);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void replenishGoal(User user, String goalId, String cardId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0");
        }

        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new IllegalArgumentException("Вклад не найден"));

        if (!goal.getOwner().equals(user)) {
            throw new IllegalStateException("Доступ запрещён");
        }

        if (goal.isClosed()) {
            throw new IllegalStateException("Цель закрыта");
        }

        if (goal.isCompleted()) {
            throw new IllegalStateException("Цель уже выполнена");
        }

        if (amount > goal.leftToComplete()) {
            throw new IllegalStateException("Сумма превышает оставшуюся до выполнения цели");
        }

        Card card = cardService.getCard(cardId);

        if (!card.getOwner().equals(user)) {
            throw new IllegalStateException("Карта принадлежит другому пользователю");
        }

        if (card.isClosed() || card.isBlocked()) {
            throw new IllegalStateException("Карта недоступна для операций");
        }

        Account sourceAccount = card.getAccount();

        if (sourceAccount.getBalance() < amount) {
            throw new IllegalStateException("Недостаточно средств на карте");
        }

        Account targetAccount = goal.getAccount();

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        transactionService.log(
                user, sourceAccount, targetAccount, Transaction.Operation.TRANSFER, amount, "Пополнение цели",
                "Пополнение цели %s на %s ₽".formatted(goal.getDisplayName(), MiscUtil.formatBalance(amount))
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void closeGoal(User user, String goalId, String cardId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new IllegalArgumentException("Цель не найдена"));

        if (goal.isClosed()) {
            throw new IllegalStateException("Цель уже закрыта");
        }

        Card card = cardService.getCard(cardId);

        if (!card.getOwner().equals(user)) {
            throw new IllegalStateException("Карта принадлежит другому пользователю");
        }

        if (card.isBlocked() || card.isClosed()) {
            throw new IllegalStateException("Карта заблокирована или закрыта");
        }

        double amount = goal.getAccount().getBalance();

        goal.getAccount().setBalance(0D);
        card.getAccount().setBalance(card.getAccount().getBalance() + amount);

        accountRepository.save(goal.getAccount());
        accountRepository.save(card.getAccount());

        goal.setClosed(true);
        goalRepository.save(goal);

        transactionService.log(
                user, goal.getAccount(), card.getAccount(), Transaction.Operation.TRANSFER, amount, "Закрытие цели",
                "Закрытие цели %s с выводом %s ₽".formatted(goal.getDisplayName(), MiscUtil.formatBalance(amount))
        );
    }
}
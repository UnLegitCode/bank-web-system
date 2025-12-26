package ru.unlegit.bank.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.unlegit.bank.entity.*;
import ru.unlegit.bank.repository.AccountRepository;
import ru.unlegit.bank.repository.DepositRepository;
import ru.unlegit.bank.util.MiscUtil;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepositService {

    DepositRepository depositRepository;
    DepositTermsService depositTermsService;
    CardService cardService;
    AccountRepository accountRepository;
    TransactionService transactionService;

    public List<Deposit> getUserDeposits(User user) {
        return depositRepository.findAllByOwnerAndClosed(user, false);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void openDeposit(User user, String termsId, String cardId, double amount) {
        Card card = cardService.getCard(cardId);

        if (!card.getOwner().equals(user)) {
            throw new SecurityException("Доступ запрещён");
        }

        if (card.isClosed()) {
            throw new IllegalStateException("Карта закрыта");
        }

        if (card.isBlocked()) {
            throw new IllegalStateException("Карта заблокирована");
        }

        DepositTerms terms = depositTermsService.getDepositTerms(termsId);

        if (amount < terms.getMinSum()) {
            throw new IllegalArgumentException("Сумма должна быть не менее " + terms.getMinSum() + " ₽");
        }

        Account sourceAccount = card.getAccount();

        if (sourceAccount.getBalance() < amount) {
            throw new IllegalStateException("Недостаточно средств на счёте");
        }

        Account depositAccount = new Account(user);

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        depositAccount.setBalance(amount);

        accountRepository.save(sourceAccount);
        depositAccount = accountRepository.save(depositAccount);

        Deposit deposit = new Deposit();

        deposit.setOwner(user);
        deposit.setAccount(depositAccount);
        deposit.setTerms(terms);
        deposit.setInitialSum(amount);
        deposit.updateNextCapitalization();

        depositRepository.save(deposit);

        transactionService.log(
                user, sourceAccount, depositAccount, Transaction.Operation.TRANSFER, amount,
                "Открытие вклада", "Открытие вклада на сумму %s ₽".formatted(MiscUtil.formatBalance(amount))
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void replenish(User user, String depositId, String cardId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0");
        }

        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Вклад не найден"));

        if (!deposit.getOwner().equals(user)) {
            throw new IllegalStateException("Доступ запрещён");
        }

        if (deposit.isClosed()) {
            throw new IllegalStateException("Вклад закрыт");
        }

        if (!deposit.getTerms().isReplenishable()) {
            throw new IllegalStateException("Пополнение по этому вкладу не разрешено");
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

        Account targetAccount = deposit.getAccount();

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        transactionService.log(
                user, sourceAccount, targetAccount, Transaction.Operation.TRANSFER, amount,
                "Пополнение вклада", "Пополнение вклада на %s ₽".formatted(MiscUtil.formatBalance(amount))
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void withdraw(User user, String depositId, String cardId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0");
        }

        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Вклад не найден"));

        if (!deposit.getOwner().equals(user)) {
            throw new IllegalStateException("Доступ запрещён");
        }

        if (deposit.isClosed()) {
            throw new IllegalStateException("Вклад закрыт");
        }

        if (!deposit.getTerms().isPartialWithdrawal()) {
            throw new IllegalStateException("Частичное снятие не разрешено");
        }

        Account depositAccount = deposit.getAccount();

        if (depositAccount.getBalance() < amount) {
            throw new IllegalStateException("Недостаточно средств на вкладе");
        }

        Card card = cardService.getCard(cardId);

        if (!card.getOwner().equals(user)) {
            throw new IllegalStateException("Карта принадлежит другому пользователю");
        }

        if (card.isClosed() || card.isBlocked()) {
            throw new IllegalStateException("Карта недоступна для операций");
        }

        Account targetAccount = card.getAccount();

        depositAccount.setBalance(depositAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        accountRepository.save(depositAccount);
        accountRepository.save(targetAccount);

        transactionService.log(
                user, depositAccount, targetAccount, Transaction.Operation.TRANSFER, amount,
                "Списание средств с вклада",
                "Списание %s ₽ с вклада на карту **** %s".formatted(
                        MiscUtil.formatBalance(amount),
                        card.getNumber().substring(card.getNumber().length() - 4)
                )
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void close(User user, String depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Вклад не найден"));

        if (!deposit.getOwner().equals(user)) {
            throw new IllegalStateException("Доступ запрещён");
        }

        if (deposit.isClosed()) {
            throw new IllegalStateException("Вклад закрыт");
        }

        Account depositAccount = deposit.getAccount();

        if (depositAccount.getBalance() > 0D) {
            throw new IllegalStateException("Перед закрытием вклада необходимо снять с него все срдества");
        }

        deposit.setClosed(true);
        depositRepository.save(deposit);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Scheduled(cron = "${deposit.capitalization.cron}")
    public void capitalizeDeposits() {
        List<Deposit> deposits = depositRepository.findActiveWithTodayCapitalization(LocalDate.now());
        List<Transaction> transactions = new LinkedList<>();

        deposits.forEach(deposit -> {
            double balance = deposit.getAccount().getBalance();
            double rate = deposit.getTerms().getInterestRate() / 100.0D;
            int months = deposit.getTerms().getCapitalizationPeriod().getMonths();

            double interest = balance * rate * months / 12.0;

            deposit.getAccount().setBalance(balance + interest);
            deposit.updateNextCapitalization();

            transactions.add(transactionService.createTransaction(
                    deposit.getOwner(), deposit.getAccount(), null,
                    Transaction.Operation.REPLENISHMENT, interest, "Зачисление процентов на вклад",
                    "Зачисление %s ₽ на вклад".formatted(MiscUtil.formatBalance(interest))
            ));
        });

        depositRepository.saveAll(deposits);
        transactionService.logAll(transactions);
    }
}
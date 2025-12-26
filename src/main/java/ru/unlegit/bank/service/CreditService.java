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
import ru.unlegit.bank.repository.CreditRepository;
import ru.unlegit.bank.util.MiscUtil;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreditService {

    CreditRepository creditRepository;
    CreditTermsService creditTermsService;
    CardService cardService;
    AccountRepository accountRepository;
    TransactionService transactionService;

    public List<Credit> getUserCredits(User user) {
        return creditRepository.findAllByOwnerAndClosed(user, false);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void openCredit(User user, String termsId, String cardId, double amount) {
        Card card = cardService.getCard(cardId);

        if (!card.getOwner().equals(user)) {
            throw new SecurityException("Доступ запрещён");
        }

        if (card.isClosed() || card.isBlocked()) {
            throw new IllegalStateException("Карта закрыта или заблокирована");
        }

        CreditTerms terms = creditTermsService.getCreditTerms(termsId);

        if (amount < terms.getMinSum() || amount > terms.getMaxSum()) {
            throw new IllegalArgumentException(
                    "Сумма должна быть от " + terms.getMinSum() + " до " + terms.getMaxSum() + " ₽"
            );
        }

        Account creditAccount = accountRepository.save(new Account(user));

        Account cardAccount = card.getAccount();

        cardAccount.setBalance(cardAccount.getBalance() + amount);

        accountRepository.save(cardAccount);

        Credit credit = new Credit();

        credit.setOwner(user);
        credit.setAccount(creditAccount);
        credit.setInterestRate(terms.getInterestRate());
        credit.setInitialSum(amount);
        credit.setTargetBalance(amount);
        credit.chargeInterest();

        creditRepository.save(credit);

        transactionService.log(
                user, creditAccount, cardAccount, Transaction.Operation.TRANSFER, amount,
                "Открытие кредита", "Открытие кредита на сумму %s ₽ под %d%c годовых".formatted(
                        MiscUtil.formatBalance(amount), credit.getInterestRate(), '%'
                )
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean repayCredit(User user, String creditId, String cardId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0");
        }

        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalStateException("Кредит не найден"));

        if (amount > credit.leftToRepaid()) {
            throw new IllegalArgumentException(
                    "Сумма погашения (" + MiscUtil.formatBalance(amount) + ") превышает долг (" + credit.formatLeftToRepaid() + ")"
            );
        }

        Card card = cardService.getCard(cardId);

        if (!card.getOwner().equals(user)) {
            throw new SecurityException("Доступ запрещён");
        }

        if (card.isClosed() || card.isBlocked()) {
            throw new IllegalStateException("Карта закрыта или заблокирована");
        }

        if (card.getAccount().getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно средств на карте");
        }

        card.getAccount().setBalance(card.getAccount().getBalance() - amount);
        credit.getAccount().setBalance(credit.getAccount().getBalance() + amount);

        accountRepository.save(card.getAccount());
        accountRepository.save(credit.getAccount());

        transactionService.log(
                user, card.getAccount(), credit.getAccount(), Transaction.Operation.TRANSFER, amount,
                "Погашение кредита", "Погашение кредита на сумму %s ₽ (%s/%s ₽)".formatted(
                        MiscUtil.formatBalance(amount), credit.getAccount().formatBalance(), credit.formatLeftToRepaid()
                )
        );

        if (credit.isRepaid()) {
            credit.setClosed(true);
            creditRepository.save(credit);
            return true;
        }

        return false;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Scheduled(cron = "${credit.interest-charge.cron}")
    public void chargeCreditInterests() {
        List<Credit> credits = creditRepository.findActiveWithTodayInterestAccrual(LocalDate.now());

        credits.forEach(Credit::chargeInterest);

        creditRepository.saveAll(credits);
    }
}
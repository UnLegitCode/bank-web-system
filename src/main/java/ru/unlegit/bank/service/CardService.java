package ru.unlegit.bank.service;

import jakarta.persistence.LockModeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unlegit.bank.auth.PasswordHandler;
import ru.unlegit.bank.dto.card.CardForm;
import ru.unlegit.bank.entity.Account;
import ru.unlegit.bank.entity.Card;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.repository.AccountRepository;
import ru.unlegit.bank.repository.CardRepository;
import ru.unlegit.bank.util.MiscUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardService {

    final AccountRepository accountRepository;
    final CardRepository cardRepository;
    final PasswordHandler passwordHandler;
    @Value("${card.number.bin}")
    String cardNumberBin;

    public List<Card> getUserCards(User owner) {
        return cardRepository.findAllByOwnerAndClosed(owner, false);
    }

    public int countActiveCards(User owner) {
        return cardRepository.countByOwnerAndBlockedAndClosed(owner, false, false);
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Card getCard(String id) {
        return cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));
    }

    public void issueCard(User user, CardForm cardForm) {
        Card card = new Card();

        card.setOwner(user);
        card.setAccount(accountRepository.save(new Account(user)));
        card.setNumber(getCardNumber(card.getAccount().getNumber()));
        card.setPinHash(passwordHandler.generateHash(cardForm.getPin()));

        cardRepository.save(card);
    }

    public String getCardNumber(long accountNumber) {
        String cardNumber = cardNumberBin + accountNumber;
        int controlDigit = MiscUtil.calculateCheckDigit(cardNumber);

        return MiscUtil.formatCardNumber(cardNumber + controlDigit);
    }

    public boolean verifyPin(Card card, String pin) {
        return passwordHandler.verifyPassword(card.getPinHash(), pin);
    }

    @Transactional
    public void changePin(String cardId, String username, String currentPin, String newPin) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!card.getOwner().getEmail().equals(username)) {
            throw new SecurityException("Доступ запрещён");
        }

        if (card.getPinHash() != null && !verifyPin(card, currentPin)) {
            throw new IllegalArgumentException("Неверный текущий ПИН-код");
        }

        if (newPin.length() != 4 || !newPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("ПИН должен содержать 4 цифры");
        }

        card.setPinHash(passwordHandler.generateHash(newPin));
        cardRepository.save(card);
    }

    @Transactional
    public void closeCard(String cardId, String email) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!card.getOwner().getEmail().equals(email)) {
            throw new SecurityException("Доступ запрещён");
        }

        if (card.isClosed()) {
            throw new IllegalStateException("Карта уже закрыта");
        }

        if (card.getAccount().getBalance() > 0D) {
            throw new IllegalStateException("Перед закрытием карты необходимо перевести все средства с нее");
        }

        card.setClosed(true);
        cardRepository.save(card);
    }

    @Transactional
    public void toggleBlock(String cardId, String email) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));

        if (!card.getOwner().getEmail().equals(email)) {
            throw new SecurityException("Доступ запрещён");
        }

        if (card.isClosed()) {
            throw new IllegalStateException("Нельзя изменить статус блокировки закрытой карты");
        }

        card.setBlocked(!card.isBlocked());
        cardRepository.save(card);
    }
}
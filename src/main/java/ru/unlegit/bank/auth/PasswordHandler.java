package ru.unlegit.bank.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class PasswordHandler {

    private final PasswordEncoder passwordEncoder;

    public String generateHash(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verifyPassword(String hash, String password) {
        return passwordEncoder.matches(password, hash);
    }
}
package ru.unlegit.bank.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.auth.PasswordHandler;
import ru.unlegit.bank.dto.auth.RegistrationStep1Data;
import ru.unlegit.bank.dto.auth.RegistrationStep2Data;
import ru.unlegit.bank.dto.auth.RegistrationStep3Data;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.exception.UserNotFoundException;
import ru.unlegit.bank.repository.UserRepository;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UserService {

    UserRepository userRepository;
    PasswordHandler passwordHandler;

    public User createUser(
            RegistrationStep1Data step1, RegistrationStep2Data step2, RegistrationStep3Data step3
    ) {
        User user = new User(step1, step2, step3.getEmail(), passwordHandler.generateHash(step3.getPassword()));

        return userRepository.save(user);
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user with email " + email));
    }

    public User getUserFromAuthentication(Authentication authentication) throws UserNotFoundException {
        return getUserByEmail(((UserDetails) authentication.getPrincipal()).getUsername());
    }
}
package ru.unlegit.bank.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.unlegit.bank.entity.User;
import ru.unlegit.bank.repository.UserRepository;

@Service
@AllArgsConstructor
public final class BankUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(User::buildDetails)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));
    }
}
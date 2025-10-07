package com.polleria.polleria.service;

import com.polleria.polleria.model.User;
import com.polleria.polleria.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty())
            return Optional.empty();
        User u = opt.get();
        if (!u.isActive())
            return Optional.empty();
        if (passwordEncoder.matches(rawPassword, u.getPasswordHash()))
            return Optional.of(u);
        return Optional.empty();
    }

    public User createUser(String username, String rawPassword, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setActive(true);
        return userRepository.save(u);
    }
}

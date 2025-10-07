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

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

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
        try {
            return userRepository.save(u);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // probablemente violacion de unique constraint en username
            throw new IllegalArgumentException("usuario ya existe");
        }
    }

    /**
     * Returns true if a user with the provided username already exists
     * (case-insensitive).
     */
    public boolean usernameExists(String username) {
        if (username == null)
            return false;
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Validates password strength for minimal requirements.
     * Returns empty Optional if OK, otherwise Optional.of(errorMessage).
     */
    public java.util.Optional<String> validatePassword(String password) {
        if (password == null || password.isBlank()) {
            return java.util.Optional.of("Password is required");
        }
        if (password.length() < 6) {
            return java.util.Optional.of("Password must be at least 6 characters");
        }
        // example additional checks (you can expand as needed)
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasDigit) {
            return java.util.Optional.of("Password should include at least one digit");
        }
        return java.util.Optional.empty();
    }

    // --- User management helpers ---
    public java.util.List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    /**
     * Update username and/or password for a user. Does not allow changing the
     * seeded 'admin' username.
     */
    public User updateUser(Integer id, String newUsername, String newPassword) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if ("admin".equals(u.getUsername())) {
            throw new IllegalArgumentException("The primary admin account cannot be modified");
        }
        if (newUsername != null && !newUsername.isBlank()) {
            // check uniqueness
            if (!u.getUsername().equals(newUsername) && usernameExists(newUsername)) {
                throw new IllegalArgumentException("Username already exists");
            }
            u.setUsername(newUsername);
        }
        if (newPassword != null && !newPassword.isBlank()) {
            java.util.Optional<String> err = validatePassword(newPassword);
            if (err.isPresent())
                throw new IllegalArgumentException(err.get());
            u.setPasswordHash(passwordEncoder.encode(newPassword));
        }
        return userRepository.save(u);
    }

    public void deleteUser(Integer id) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if ("admin".equals(u.getUsername())) {
            throw new IllegalArgumentException("The primary admin account cannot be deleted");
        }
        // soft-delete: mark as inactive so it remains visible but disabled
        u.setActive(false);
        userRepository.save(u);
    }

    /**
     * Reactivate a previously deactivated user.
     */
    public User reactivateUser(Integer id) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.setActive(true);
        return userRepository.save(u);
    }

    public java.util.List<User> findUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
}

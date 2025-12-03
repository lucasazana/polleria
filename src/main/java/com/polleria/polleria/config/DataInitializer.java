package com.polleria.polleria.config;

import com.polleria.polleria.model.User;
import com.polleria.polleria.repository.UserRepository;
import com.polleria.polleria.service.InventoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InventoryService inventoryService;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
            InventoryService inventoryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.inventoryService = inventoryService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario admin si no existe
        if (userRepository.findByUsername("admin").isEmpty()) {
            String raw = adminPassword;
            if (raw == null || raw.isBlank()) {
                raw = generateRandomPassword(12);
                System.out.println("Generated admin password: " + raw);
            }

            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode(raw));
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
        }

        // Inicializar categorías por defecto
        inventoryService.initializeDefaultCategories();
    }

    private String generateRandomPassword(int length) {
        SecureRandom rnd = new SecureRandom();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}

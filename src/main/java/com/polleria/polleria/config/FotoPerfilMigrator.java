package com.polleria.polleria.config;

import com.polleria.polleria.model.User;
import com.polleria.polleria.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
public class FotoPerfilMigrator {
    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void migrateFotos() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getFotoPerfilPath() != null && !user.getFotoPerfilPath().isEmpty()) {
                String ruta = user.getFotoPerfilPath();
                try {
                    File file = new File("src/main/webapp" + ruta);
                    if (file.exists()) {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        user.setFotoPerfil(bytes);
                        user.setFotoPerfilPath(null); // Limpia el campo temporal
                        userRepository.save(user);
                        System.out.println("Migrada foto para usuario: " + user.getUsername());
                    } else {
                        System.err.println("Archivo no encontrado para usuario: " + user.getUsername() + " en " + ruta);
                    }
                } catch (IOException e) {
                    System.err.println("Error migrando foto para usuario: " + user.getUsername());
                }
            }
        }
    }
}

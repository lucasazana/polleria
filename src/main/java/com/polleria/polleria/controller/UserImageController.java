package com.polleria.polleria.controller;

import com.polleria.polleria.model.User;
import com.polleria.polleria.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@Controller
public class UserImageController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/foto/{id}")
    public ResponseEntity<byte[]> verFotoPerfil(@PathVariable Integer id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty() || userOpt.get().getFotoPerfil() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] imagen = userOpt.get().getFotoPerfil();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=perfil.jpg")
                .contentType(MediaType.IMAGE_JPEG)
                .body(imagen);
    }
}

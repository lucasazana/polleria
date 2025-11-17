package com.polleria.polleria.controller;

import com.polleria.polleria.model.User;
import com.polleria.polleria.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class ProfileController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/foto-perfil")
    public String subirFotoPerfil(@RequestParam("foto") MultipartFile foto,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();
        if (foto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selecciona una imagen válida.");
            return "redirect:/user/dashboard";
        }
        try {
            user.setFotoPerfil(foto.getBytes());
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Foto de perfil actualizada.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al subir la imagen.");
        }
        return "redirect:/user/dashboard";
    }
}

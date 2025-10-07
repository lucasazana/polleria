package com.polleria.polleria.controller;

import com.polleria.polleria.model.User;
import com.polleria.polleria.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object role = session.getAttribute("role");
            if (role != null) {
                String r = role.toString();
                if ("ADMIN".equalsIgnoreCase(r))
                    return "redirect:/admin/dashboard";
                return "redirect:/employee/dashboard";
            }
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request,
            Model model) {
        Optional<User> opt = authService.authenticate(username, password);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Credenciales inválidas");
            return "login";
        }
        User user = opt.get();
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        if ("ADMIN".equalsIgnoreCase(user.getRole()))
            return "redirect:/admin/dashboard";
        return "redirect:/employee/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        if (s != null)
            s.invalidate();
        return "redirect:/login";
    }
}

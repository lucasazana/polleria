package com.polleria.polleria.controller;

import com.polleria.polleria.service.AuthService;
import com.polleria.polleria.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    private boolean isAdminSession(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null && "ADMIN".equals(role.toString());
    }

    @GetMapping("/admin/create")
    public String showCreateForm(HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        return "admin_create";
    }

    @PostMapping("/admin/create")
    public String handleCreate(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String password,
            Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        // Basic checks (you can expand validation)
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Username and password are required");
            return "admin_create";
        }

        User created = authService.createUser(username.trim(), password, "ADMIN");
        model.addAttribute("success", "Admin user '" + created.getUsername() + "' created successfully.");
        return "admin_create";
    }
}

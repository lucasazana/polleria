package com.polleria.polleria.controller;

import com.polleria.polleria.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    // helper: devuelve redirect si la sesion no es admin, o null si es admin
    private String requireAdmin(HttpSession session) {
        return isAdminSession(session) ? null : "redirect:/login";
    }

    @FunctionalInterface
    private interface Action {
        void run() throws Exception;
    }

    // helper para ejecutar una accion y redirigir con flash messages
    private String executeAndRedirect(Action action, RedirectAttributes ra, String successMsg, String redirectTo) {
        try {
            action.run();
            if (successMsg != null && !successMsg.isBlank())
                ra.addFlashAttribute("success", successMsg);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:" + redirectTo;
    }

    @GetMapping("/admin/create")
    public String showCreateForm(HttpSession session, Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return "admin/admin_create";
    }

    // Map the administrators card to the create form for now
    @GetMapping("/admin/administrators")
    public String administrators(HttpSession session, Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        model.addAttribute("admins", authService.findUsersByRole(AuthService.ROLE_ADMIN));
        return "admin/admins";
    }

    @GetMapping("/admin/employees")
    public String employees(HttpSession session) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        return "admin/employees"; // placeholder view
    }

    @GetMapping("/admin/inventory")
    public String inventory(HttpSession session) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        return "admin/inventory"; // placeholder view
    }

    @GetMapping("/admin/attendance")
    public String attendance(HttpSession session) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        return "admin/attendance"; // placeholder view
    }

    @GetMapping("/admin/reports")
    public String reports(HttpSession session) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        return "admin/reports"; // placeholder view
    }

    // ----- User management (gestion de usuarios) -----
    @GetMapping("/admin/users")
    public String listUsers(HttpSession session, org.springframework.ui.Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        model.addAttribute("users", authService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/admin/users/create")
    public String showUserCreate(HttpSession session) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return "admin/user_form"; // form used for create
    }

    @PostMapping("/admin/users/create")
    public String handleUserCreate(HttpSession session,
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Username and password are required");
            return "redirect:/admin/users/create";
        }
        if (authService.usernameExists(username.trim())) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/admin/users/create";
        }
        // execute create and redirect with helper
        return executeAndRedirect(() -> authService.createUser(username.trim(), password, AuthService.ROLE_EMPLOYEE),
                redirectAttributes, "Usuario creado", "/admin/users");
    }

    @GetMapping("/admin/users/{id}/edit")
    public String showUserEdit(HttpSession session, @org.springframework.web.bind.annotation.PathVariable Integer id,
            org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        try {
            java.util.Optional<com.polleria.polleria.model.User> u = authService.findById(id);
            if (u.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/users";
            }
            model.addAttribute("user", u.get());
            return "admin/user_form"; // same form, will detect edit by presence of user
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/admin/users/{id}/edit")
    public String handleUserEdit(HttpSession session,
            @org.springframework.web.bind.annotation.PathVariable Integer id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return executeAndRedirect(() -> authService.updateUser(id, username, password), redirectAttributes,
                "Usuario actualizado", "/admin/users");
    }

    @PostMapping("/admin/users/{id}/delete")
    public String handleUserDelete(HttpSession session,
            @org.springframework.web.bind.annotation.PathVariable Integer id, RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return executeAndRedirect(() -> authService.deleteUser(id), redirectAttributes, "Usuario desactivado",
                "/admin/users");
    }

    @PostMapping("/admin/users/{id}/reactivate")
    public String handleUserReactivate(HttpSession session,
            @org.springframework.web.bind.annotation.PathVariable Integer id, RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return executeAndRedirect(() -> authService.reactivateUser(id), redirectAttributes, "Usuario reactivado",
                "/admin/users");
    }

    @PostMapping("/admin/create")
    public String handleCreate(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;

        // Basic checks (you can expand validation)
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Username and password are required");
            return "redirect:/admin/create";
        }

        String uname = username.trim();
        // Check username uniqueness
        if (authService.usernameExists(uname)) {
            redirectAttributes.addFlashAttribute("error", "Username already exists: " + uname);
            return "redirect:/admin/create";
        }

        // Validate password strength
        java.util.Optional<String> passError = authService.validatePassword(password);
        if (passError.isPresent()) {
            redirectAttributes.addFlashAttribute("error", passError.get());
            return "redirect:/admin/create";
        }

        // create admin
        return executeAndRedirect(() -> authService.createUser(uname, password, AuthService.ROLE_ADMIN),
                redirectAttributes,
                "Admin user '" + uname + "' created successfully.", "/admin/create");
    }
}

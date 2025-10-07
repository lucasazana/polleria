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

    // verifica si la sesion actual pertenece a un admin
    private boolean isAdminSession(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null && "ADMIN".equals(role.toString());
    }

    // devuelve redirect si la sesion no es admin o null si es admin
    private String requireAdmin(HttpSession session) {
        return isAdminSession(session) ? null : "redirect:/login";
    }

    @FunctionalInterface
    private interface Action {
        void run() throws Exception;
    }

    // ejecuta una accion y redirige con mensajes flash segun el resultado
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

    // ruta principal de admin - redirige al dashboard
    @GetMapping("/admin")
    public String admin(HttpSession session) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return "admin/dashboard";
    }

    // muestra el dashboard principal de admin
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return "admin/dashboard";
    }

    // muestra el formulario para crear nuevos admins
    @GetMapping("/admin/crear")
    public String showCreateForm(HttpSession session, Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return "admin/admin_create";
    }

    // muestra la lista de administradores existentes
    @GetMapping("/admin/administradores")
    public String administrators(HttpSession session, Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        model.addAttribute("admins", authService.findUsersByRole(AuthService.ROLE_ADMIN));
        return "admin/admins";
    }

    // muestra la lista de empleados existentes
    @GetMapping("/admin/empleados")
    public String employees(HttpSession session, Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        model.addAttribute("employees", authService.findUsersByRole(AuthService.ROLE_USER));
        return "admin/employees";
    }

    // muestra el formulario para crear nuevos empleados
    @GetMapping("/admin/empleados/crear")
    public String showCreateEmployeeForm(HttpSession session, Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return "admin/employee_form";
    }

    // vista de inventario (redirige al nuevo dashboard)
    @GetMapping("/admin/inventario")
    public String inventory(HttpSession session) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        return "redirect:/admin/inventario/dashboard";
    }

    // vista de asistencia
    @GetMapping("/admin/asistencia")
    public String attendance(HttpSession session) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        return "admin/attendance";
    }

    // vista de reportes
    @GetMapping("/admin/reportes")
    public String reports(HttpSession session) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        return "admin/reports";
    }

    // procesa la creacion de un nuevo empleado
    @PostMapping("/admin/empleados/crear")
    public String handleCreateEmployee(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;

        // validaciones basicas
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Se requieren nombre de usuario y contraseña");
            return "redirect:/admin/empleados/crear";
        }

        String uname = username.trim();
        // verificar que el nombre de usuario no exista
        if (authService.usernameExists(uname)) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe: " + uname);
            return "redirect:/admin/empleados/crear";
        }

        // validar fortaleza de la contraseña
        java.util.Optional<String> passError = authService.validatePassword(password);
        if (passError.isPresent()) {
            redirectAttributes.addFlashAttribute("error", passError.get());
            return "redirect:/admin/empleados/crear";
        }

        // crear empleado y redirigir con mensaje
        return executeAndRedirect(() -> authService.createUser(uname, password, AuthService.ROLE_USER),
                redirectAttributes,
                "Usuario '" + uname + "' creado exitosamente.", "/admin/empleados");
    }

    // muestra el formulario para editar un empleado existente
    @GetMapping("/admin/empleados/{id}/editar")
    public String showEmployeeEdit(HttpSession session,
            @org.springframework.web.bind.annotation.PathVariable Integer id,
            org.springframework.ui.Model model, RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        try {
            java.util.Optional<com.polleria.polleria.model.User> u = authService.findById(id);
            if (u.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/empleados";
            }
            model.addAttribute("user", u.get());
            return "admin/employee_form";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/empleados";
        }
    }

    // procesa la edicion de un empleado existente
    @PostMapping("/admin/empleados/{id}/editar")
    public String handleEmployeeEdit(HttpSession session,
            @org.springframework.web.bind.annotation.PathVariable Integer id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return executeAndRedirect(() -> authService.updateUser(id, username, password), redirectAttributes,
                "Usuario actualizado", "/admin/empleados");
    }

    // desactiva un empleado
    @PostMapping("/admin/empleados/{id}/eliminar")
    public String handleEmployeeDelete(HttpSession session,
            @org.springframework.web.bind.annotation.PathVariable Integer id, RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return executeAndRedirect(() -> authService.deleteUser(id), redirectAttributes, "Usuario desactivado",
                "/admin/empleados");
    }

    // reactiva un empleado
    @PostMapping("/admin/empleados/{id}/reactivar")
    public String handleEmployeeReactivate(HttpSession session,
            @org.springframework.web.bind.annotation.PathVariable Integer id, RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return executeAndRedirect(() -> authService.reactivateUser(id), redirectAttributes, "Usuario reactivado",
                "/admin/empleados");
    }

    // procesa la creacion de un nuevo administrador
    @PostMapping("/admin/crear")
    public String handleCreate(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;

        // validaciones basicas
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Se requieren nombre de usuario y contraseña");
            return "redirect:/admin/crear";
        }

        String uname = username.trim();
        // verificar que el nombre de usuario no exista
        if (authService.usernameExists(uname)) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe: " + uname);
            return "redirect:/admin/crear";
        }

        // validar fortaleza de la contraseña
        java.util.Optional<String> passError = authService.validatePassword(password);
        if (passError.isPresent()) {
            redirectAttributes.addFlashAttribute("error", passError.get());
            return "redirect:/admin/crear";
        }

        // crear admin y redirigir con mensaje
        return executeAndRedirect(() -> authService.createUser(uname, password, AuthService.ROLE_ADMIN),
                redirectAttributes,
                "Admin user '" + uname + "' created successfully.", "/admin/crear");
    }
}

package com.polleria.polleria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Always show the login view at root; authentication/redirect happens on POST
        // /login
        return "login"; // resolves to /WEB-INF/views/login.jsp
    }

    @GetMapping("/admin")
    public String admin() {
        return "DashboardAdmin"; // Busca /WEB-INF/views/dashboardadmin.jsp
    }
}

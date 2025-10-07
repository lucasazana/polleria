package com.polleria.polleria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin/dashboard"; // maps to /WEB-INF/views/admin/dashboard.jsp
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}

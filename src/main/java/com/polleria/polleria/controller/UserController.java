package com.polleria.polleria.controller;

import com.polleria.polleria.model.Attendance;
import com.polleria.polleria.model.User;
import com.polleria.polleria.repository.AttendanceRepository;
import com.polleria.polleria.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                // Estado de asistencia del día
                Optional<Attendance> asistenciaOpt = attendanceRepository.findByEmpleadoAndFecha(user, LocalDate.now());
                asistenciaOpt.ifPresent(a -> model.addAttribute("asistencia", a));
            }
        }
        return "user/dashboard";
    }
}

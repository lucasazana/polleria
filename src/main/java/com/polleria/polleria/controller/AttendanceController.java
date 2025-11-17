package com.polleria.polleria.controller;

import com.polleria.polleria.model.Attendance;
import com.polleria.polleria.model.User;
import com.polleria.polleria.repository.AttendanceRepository;
import com.polleria.polleria.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Controller
@RequestMapping("/user/asistencia")
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/entrada")
    public String marcarEntrada(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();
        LocalDate hoy = LocalDate.now();
        Optional<Attendance> asistenciaOpt = attendanceRepository.findByEmpleadoAndFecha(user, hoy);
        if (asistenciaOpt.isPresent() && asistenciaOpt.get().getHoraEntrada() != null) {
            redirectAttributes.addFlashAttribute("error", "Ya marcaste tu entrada hoy.");
            return "redirect:/user/dashboard";
        }
        Attendance asistencia = asistenciaOpt.orElse(new Attendance());
        asistencia.setEmpleado(user);
        asistencia.setFecha(hoy);
        asistencia.setHoraEntrada(LocalTime.now());
        attendanceRepository.save(asistencia);
    redirectAttributes.addFlashAttribute("success", "Asistencia registrada.");
        return "redirect:/user/dashboard";
    }

    @PostMapping("/refrigerio")
    public String marcarRefrigerio(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();
        LocalDate hoy = LocalDate.now();
        Optional<Attendance> asistenciaOpt = attendanceRepository.findByEmpleadoAndFecha(user, hoy);
        if (asistenciaOpt.isEmpty() || asistenciaOpt.get().getHoraEntrada() == null) {
            redirectAttributes.addFlashAttribute("error", "Debes marcar tu entrada antes.");
            return "redirect:/user/dashboard";
        }
        Attendance asistencia = asistenciaOpt.get();
        if (asistencia.getHoraRefrigerio() != null) {
            redirectAttributes.addFlashAttribute("error", "Ya marcaste tu refrigerio hoy.");
            return "redirect:/user/dashboard";
        }
        asistencia.setHoraRefrigerio(LocalTime.now());
        attendanceRepository.save(asistencia);
    redirectAttributes.addFlashAttribute("success", "¡Hora de tu refrigerio registrada! Disfruta tu descanso.");
        return "redirect:/user/dashboard";
    }

    @PostMapping("/salida")
    public String marcarSalida(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();
        LocalDate hoy = LocalDate.now();
        Optional<Attendance> asistenciaOpt = attendanceRepository.findByEmpleadoAndFecha(user, hoy);
        if (asistenciaOpt.isEmpty() || asistenciaOpt.get().getHoraEntrada() == null) {
            redirectAttributes.addFlashAttribute("error", "Debes marcar tu entrada antes.");
            return "redirect:/user/dashboard";
        }
        Attendance asistencia = asistenciaOpt.get();
        if (asistencia.getHoraSalida() != null) {
            redirectAttributes.addFlashAttribute("error", "Ya marcaste tu salida hoy.");
            return "redirect:/user/dashboard";
        }
        asistencia.setHoraSalida(LocalTime.now());
        attendanceRepository.save(asistencia);
    redirectAttributes.addFlashAttribute("success", "Salida registrada. ¡Buen trabajo hoy!");
        return "redirect:/user/dashboard";
    }
}


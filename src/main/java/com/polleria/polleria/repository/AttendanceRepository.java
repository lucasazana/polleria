package com.polleria.polleria.repository;

import com.polleria.polleria.model.Attendance;
import com.polleria.polleria.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Optional<Attendance> findByEmpleadoAndFecha(User empleado, LocalDate fecha);
    List<Attendance> findAllByEmpleado(User empleado);
    List<Attendance> findAllByFecha(LocalDate fecha);
    List<Attendance> findAllByEmpleadoAndFechaBetween(User empleado, LocalDate desde, LocalDate hasta);
    List<Attendance> findAllByFechaBetween(LocalDate desde, LocalDate hasta);
}


package com.polleria.polleria.controller;

import com.polleria.polleria.service.AuthService;
import com.polleria.polleria.service.InventoryService;
import com.polleria.polleria.repository.AttendanceRepository;
import com.polleria.polleria.repository.UserRepository;
import com.polleria.polleria.repository.InventoryMovementRepository;
import com.polleria.polleria.model.Attendance;
import com.polleria.polleria.model.User;
import com.polleria.polleria.model.Product;
import com.polleria.polleria.model.Category;
import com.polleria.polleria.model.InventoryMovement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AdminController {

    // Exporta el reporte de asistencia filtrado a PDF
    @GetMapping("/admin/reportes/asistencia/pdf")
    public void exportarAsistenciaPdf(
            @RequestParam(value = "empleadoId", required = false) Integer empleadoId,
            @RequestParam(value = "desde", required = false) String desdeStr,
            @RequestParam(value = "hasta", required = false) String hastaStr,
            HttpServletResponse response) throws Exception {
        List<Attendance> asistencias;
        java.time.LocalDate desde = null;
        java.time.LocalDate hasta = null;
        if (desdeStr != null && !desdeStr.isBlank()) {
            desde = java.time.LocalDate.parse(desdeStr);
        }
        if (hastaStr != null && !hastaStr.isBlank()) {
            hasta = java.time.LocalDate.parse(hastaStr);
        }
        if (empleadoId != null && desde != null && hasta != null) {
            User empleado = userRepository.findById(empleadoId).orElse(null);
            asistencias = (empleado != null)
                    ? attendanceRepository.findAllByEmpleadoAndFechaBetween(empleado, desde, hasta)
                    : java.util.Collections.emptyList();
        } else if (desde != null && hasta != null) {
            asistencias = attendanceRepository.findAllByFechaBetween(desde, hasta);
        } else {
            asistencias = java.util.Collections.emptyList();
        }

        response.setContentType("application/pdf");
        String fechaHoy = java.time.LocalDate.now().toString();
        String nombreArchivo = "asistencias_";
        if (empleadoId != null) {
            User empleado = userRepository.findById(empleadoId).orElse(null);
            if (empleado != null) {
                nombreArchivo += empleado.getUsername() + "_";
            }
        }
        nombreArchivo += fechaHoy + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título centrado y grande
        Paragraph titulo = new Paragraph("Reporte de Asistencias")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        // Fecha de generación
        Paragraph fecha = new Paragraph("Generado el: " + fechaHoy)
                .setFontSize(10)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                .setMarginBottom(15);
        document.add(fecha);

        float[] columnWidths = { 120, 80, 80, 80, 80, 150 };
        Table table = new Table(columnWidths);
        // Encabezados con fondo gris y texto blanco
        String[] headers = { "Empleado", "Fecha", "Entrada", "Refrigerio", "Salida", "Observaciones" };
        for (String h : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(h).setBold().setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE));
            cell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY);
            cell.setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            cell.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));
            table.addHeaderCell(cell);
        }
        // Filas de datos
        for (Attendance a : asistencias) {
            table.addCell(new Cell().add(new Paragraph(a.getEmpleado().getUsername()))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            table.addCell(new Cell().add(new Paragraph(a.getFecha() != null ? a.getFecha().toString() : ""))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            table.addCell(new Cell().add(new Paragraph(a.getHoraEntrada() != null ? a.getHoraEntrada().toString() : ""))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            table.addCell(
                    new Cell().add(new Paragraph(a.getHoraRefrigerio() != null ? a.getHoraRefrigerio().toString() : ""))
                            .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            table.addCell(new Cell().add(new Paragraph(a.getHoraSalida() != null ? a.getHoraSalida().toString() : ""))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            table.addCell(new Cell().add(new Paragraph(a.getObservaciones() != null ? a.getObservaciones() : ""))
                    .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
        }
        table.setMarginTop(10);
        document.add(table);
        document.close();
    }

    private final AuthService authService;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final InventoryMovementRepository movementRepository;

    public AdminController(AuthService authService, AttendanceRepository attendanceRepository,
            UserRepository userRepository, InventoryService inventoryService,
            InventoryMovementRepository movementRepository) {
        this.authService = authService;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
        this.movementRepository = movementRepository;
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
    public String attendance(HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }
        LocalDate hoy = LocalDate.now();
        List<User> empleados = userRepository.findByRole("USER");
        List<Attendance> asistencias = attendanceRepository.findAllByFecha(hoy);
        model.addAttribute("empleados", empleados);
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("fechaHoy", hoy);
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

    // Reportes de inventario - Vista
    @GetMapping("/admin/reportes/inventario")
    public String reportesInventario(
            @RequestParam(value = "tipoReporte", required = false, defaultValue = "productos") String tipoReporte,
            @RequestParam(value = "categoriaId", required = false) Integer categoriaId,
            @RequestParam(value = "desde", required = false) String desdeStr,
            @RequestParam(value = "hasta", required = false) String hastaStr,
            HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        // Obtener categorías para el filtro
        List<Category> categorias = inventoryService.getAllActiveCategories();
        model.addAttribute("categorias", categorias);
        model.addAttribute("tipoReporte", tipoReporte);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("desde", desdeStr);
        model.addAttribute("hasta", hastaStr);

        // Estadísticas generales
        model.addAttribute("totalProductos", inventoryService.getActiveProductsCount());
        model.addAttribute("productosStockBajo", inventoryService.getLowStockProductsCount());
        model.addAttribute("productosPorVencer", inventoryService.getProductsExpiringInDaysCount(30));
        model.addAttribute("valorTotal", inventoryService.getTotalInventoryValue());

        // Obtener datos según el tipo de reporte
        List<Product> productos = null;
        List<InventoryMovement> movimientos = null;

        switch (tipoReporte) {
            case "stockBajo":
                productos = inventoryService.getProductsWithLowStock();
                break;
            case "porVencer":
                productos = inventoryService.getProductsExpiringBefore(LocalDate.now().plusDays(30));
                break;
            case "movimientos":
                if (desdeStr != null && !desdeStr.isBlank() && hastaStr != null && !hastaStr.isBlank()) {
                    LocalDateTime desde = LocalDate.parse(desdeStr).atStartOfDay();
                    LocalDateTime hasta = LocalDate.parse(hastaStr).atTime(23, 59, 59);
                    movimientos = movementRepository.findMovementsByDateRange(desde, hasta);
                }
                break;
            default: // productos
                if (categoriaId != null) {
                    productos = inventoryService.getProductsByCategory(categoriaId);
                } else {
                    productos = inventoryService.getAllActiveProducts();
                }
                break;
        }

        model.addAttribute("productos", productos);
        model.addAttribute("movimientos", movimientos);

        return "admin/reportes_inventario";
    }

    // Exportar reporte de inventario a PDF
    @GetMapping("/admin/reportes/inventario/pdf")
    public void exportarInventarioPdf(
            @RequestParam(value = "tipoReporte", required = false, defaultValue = "productos") String tipoReporte,
            @RequestParam(value = "categoriaId", required = false) Integer categoriaId,
            @RequestParam(value = "desde", required = false) String desdeStr,
            @RequestParam(value = "hasta", required = false) String hastaStr,
            HttpServletResponse response) throws Exception {

        // Obtener datos según el tipo de reporte
        List<Product> productos = null;
        List<InventoryMovement> movimientos = null;

        switch (tipoReporte) {
            case "stockBajo":
                productos = inventoryService.getProductsWithLowStock();
                break;
            case "porVencer":
                productos = inventoryService.getProductsExpiringBefore(LocalDate.now().plusDays(30));
                break;
            case "movimientos":
                if (desdeStr != null && !desdeStr.isBlank() && hastaStr != null && !hastaStr.isBlank()) {
                    LocalDateTime desde = LocalDate.parse(desdeStr).atStartOfDay();
                    LocalDateTime hasta = LocalDate.parse(hastaStr).atTime(23, 59, 59);
                    movimientos = movementRepository.findMovementsByDateRange(desde, hasta);
                }
                break;
            default:
                if (categoriaId != null) {
                    productos = inventoryService.getProductsByCategory(categoriaId);
                } else {
                    productos = inventoryService.getAllActiveProducts();
                }
                break;
        }

        // Configurar respuesta HTTP
        response.setContentType("application/pdf");
        String fechaHoy = LocalDate.now().toString();
        String nombreArchivo = "inventario_" + tipoReporte + "_" + fechaHoy + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);

        // Crear documento PDF
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        String titulo = "Reporte de Inventario";
        switch (tipoReporte) {
            case "stockBajo":
                titulo = "Productos con Stock Bajo";
                break;
            case "porVencer":
                titulo = "Productos por Vencer";
                break;
            case "movimientos":
                titulo = "Movimientos de Inventario";
                break;
            default:
                titulo = "Listado de Productos";
                break;
        }

        Paragraph tituloP = new Paragraph(titulo)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(tituloP);

        // Fecha de generación
        Paragraph fecha = new Paragraph("Generado el: " + fechaHoy)
                .setFontSize(10)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                .setMarginBottom(15);
        document.add(fecha);

        if ("movimientos".equals(tipoReporte) && movimientos != null) {
            // Tabla de movimientos
            float[] columnWidths = { 80, 100, 60, 50, 60, 60, 100, 70 };
            Table table = new Table(columnWidths);

            String[] headers = { "Fecha", "Producto", "Tipo", "Cantidad", "Stock Ant.", "Stock Nuevo", "Motivo",
                    "Usuario" };
            for (String h : headers) {
                Cell cell = new Cell().add(new Paragraph(h).setBold()
                        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE).setFontSize(9));
                cell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY);
                cell.setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
                cell.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));
                table.addHeaderCell(cell);
            }

            for (InventoryMovement m : movimientos) {
                table.addCell(new Cell().add(new Paragraph(
                        m.getFechaMovimiento() != null ? m.getFechaMovimiento().toLocalDate().toString() : "")
                        .setFontSize(8)).setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(new Cell().add(new Paragraph(m.getProduct().getNombre()).setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(new Cell().add(new Paragraph(m.getTipoMovimiento().toString()).setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(new Cell().add(new Paragraph(m.getCantidad().toString()).setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(new Cell().add(new Paragraph(m.getStockAnterior().toString()).setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(new Cell().add(new Paragraph(m.getStockNuevo().toString()).setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(new Cell().add(new Paragraph(m.getMotivo() != null ? m.getMotivo() : "").setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(
                        new Cell().add(new Paragraph(m.getUsuarioResponsable() != null ? m.getUsuarioResponsable() : "")
                                .setFontSize(8)).setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            }
            document.add(table);
        } else if (productos != null) {
            // Tabla de productos
            float[] columnWidths = { 120, 80, 60, 60, 70, 70, 80 };
            Table table = new Table(columnWidths);

            String[] headers = { "Producto", "Categoría", "Stock", "Mínimo", "Precio", "Vencimiento", "Ubicación" };
            for (String h : headers) {
                Cell cell = new Cell().add(new Paragraph(h).setBold()
                        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE).setFontSize(9));
                cell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY);
                cell.setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
                cell.setBorder(new com.itextpdf.layout.borders.SolidBorder(1));
                table.addHeaderCell(cell);
            }

            for (Product p : productos) {
                // Resaltar productos con stock bajo
                boolean stockBajo = p.getStockActual().compareTo(p.getStockMinimo()) <= 0;

                Cell cellNombre = new Cell().add(new Paragraph(p.getNombre()).setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1));
                if (stockBajo)
                    cellNombre.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
                table.addCell(cellNombre);

                table.addCell(new Cell()
                        .add(new Paragraph(p.getCategory() != null ? p.getCategory().getNombre() : "").setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(
                        new Cell().add(new Paragraph(p.getStockActual() + " " + p.getUnidadMedida()).setFontSize(8))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(
                        new Cell().add(new Paragraph(p.getStockMinimo() + " " + p.getUnidadMedida()).setFontSize(8))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(new Cell().add(new Paragraph("S/ " + p.getPrecioUnitario()).setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(new Cell()
                        .add(new Paragraph(p.getFechaVencimiento() != null ? p.getFechaVencimiento().toString() : "")
                                .setFontSize(8))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
                table.addCell(
                        new Cell().add(new Paragraph(p.getUbicacion() != null ? p.getUbicacion() : "").setFontSize(8))
                                .setBorder(new com.itextpdf.layout.borders.SolidBorder(1)));
            }
            document.add(table);

            // Resumen al final
            document.add(new Paragraph("\n"));
            BigDecimal valorTotal = inventoryService.getTotalInventoryValue();
            document.add(new Paragraph("Total de productos: " + productos.size()).setFontSize(10));
            document.add(new Paragraph("Valor total del inventario: S/ " + valorTotal).setFontSize(10).setBold());
        }

        document.close();
    }

    // Reportes de asistencia
    @GetMapping("/admin/reportes/asistencia")
    public String reportesAsistencia(
            @RequestParam(value = "empleadoId", required = false) Integer empleadoId,
            @RequestParam(value = "desde", required = false) String desdeStr,
            @RequestParam(value = "hasta", required = false) String hastaStr,
            org.springframework.ui.Model model) {
        List<User> empleados = userRepository.findByRole("USER");
        List<Attendance> asistencias;
        java.time.LocalDate desde = null;
        java.time.LocalDate hasta = null;
        if (desdeStr != null && !desdeStr.isBlank()) {
            desde = java.time.LocalDate.parse(desdeStr);
        }
        if (hastaStr != null && !hastaStr.isBlank()) {
            hasta = java.time.LocalDate.parse(hastaStr);
        }
        if (empleadoId != null && desde != null && hasta != null) {
            User empleado = userRepository.findById(empleadoId).orElse(null);
            asistencias = (empleado != null)
                    ? attendanceRepository.findAllByEmpleadoAndFechaBetween(empleado, desde, hasta)
                    : java.util.Collections.emptyList();
        } else if (desde != null && hasta != null) {
            asistencias = attendanceRepository.findAllByFechaBetween(desde, hasta);
        } else {
            asistencias = java.util.Collections.emptyList();
        }
        model.addAttribute("empleados", empleados);
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("empleadoId", empleadoId);
        model.addAttribute("desde", desdeStr);
        model.addAttribute("hasta", hastaStr);
        return "admin/reportes_asistencia";
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

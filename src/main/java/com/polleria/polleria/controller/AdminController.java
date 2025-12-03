
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
import java.util.Collections;
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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.borders.SolidBorder;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AdminController {

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

    // ===================== MÉTODOS AUXILIARES =====================

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

    // parsea una fecha desde String, retorna null si es vacío o nulo
    private LocalDate parseFecha(String fechaStr) {
        return (fechaStr != null && !fechaStr.isBlank()) ? LocalDate.parse(fechaStr) : null;
    }

    // obtiene asistencias filtradas por empleado y rango de fechas
    private List<Attendance> filtrarAsistencias(Integer empleadoId, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null) {
            if (empleadoId != null) {
                User empleado = userRepository.findById(empleadoId).orElse(null);
                return (empleado != null)
                        ? attendanceRepository.findAllByEmpleadoAndFechaBetween(empleado, desde, hasta)
                        : Collections.emptyList();
            }
            return attendanceRepository.findAllByFechaBetween(desde, hasta);
        }
        return Collections.emptyList();
    }

    // clase auxiliar para agrupar productos y movimientos
    private static class ReporteInventarioData {
        List<Product> productos;
        List<InventoryMovement> movimientos;
    }

    // obtiene datos de inventario según el tipo de reporte
    private ReporteInventarioData obtenerDatosInventario(String tipoReporte, Integer categoriaId,
            String desdeStr, String hastaStr) {
        ReporteInventarioData data = new ReporteInventarioData();

        switch (tipoReporte) {
            case "stockBajo":
                data.productos = inventoryService.getProductsWithLowStock();
                break;
            case "porVencer":
                data.productos = inventoryService.getProductsExpiringBefore(LocalDate.now().plusDays(30));
                break;
            case "movimientos":
                if (desdeStr != null && !desdeStr.isBlank() && hastaStr != null && !hastaStr.isBlank()) {
                    LocalDateTime desde = LocalDate.parse(desdeStr).atStartOfDay();
                    LocalDateTime hasta = LocalDate.parse(hastaStr).atTime(23, 59, 59);
                    data.movimientos = movementRepository.findMovementsByDateRange(desde, hasta);
                }
                break;
            default:
                data.productos = (categoriaId != null)
                        ? inventoryService.getProductsByCategory(categoriaId)
                        : inventoryService.getAllActiveProducts();
                break;
        }
        return data;
    }

    // obtiene el título del reporte según el tipo
    private String obtenerTituloReporte(String tipoReporte) {
        switch (tipoReporte) {
            case "stockBajo":
                return "Productos con Stock Bajo";
            case "porVencer":
                return "Productos por Vencer";
            case "movimientos":
                return "Movimientos de Inventario";
            default:
                return "Listado de Productos";
        }
    }

    // ===================== MÉTODOS AUXILIARES PDF =====================

    // configura la respuesta HTTP para PDF
    private void configurarRespuestaPdf(HttpServletResponse response, String nombreArchivo) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo);
    }

    // crea el título del documento PDF
    private void agregarTituloPdf(Document document, String titulo) {
        Paragraph tituloP = new Paragraph(titulo)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(tituloP);
    }

    // agrega la fecha de generación al PDF
    private void agregarFechaPdf(Document document) {
        Paragraph fecha = new Paragraph("Generado el: " + LocalDate.now().toString())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(15);
        document.add(fecha);
    }

    // crea una celda de encabezado con estilo
    private Cell crearCeldaEncabezado(String texto) {
        Cell cell = new Cell().add(new Paragraph(texto).setBold().setFontColor(ColorConstants.WHITE).setFontSize(9));
        cell.setBackgroundColor(ColorConstants.DARK_GRAY);
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setBorder(new SolidBorder(1));
        return cell;
    }

    // crea una celda de datos normal
    private Cell crearCeldaDato(String texto) {
        return new Cell().add(new Paragraph(texto != null ? texto : "").setFontSize(8))
                .setBorder(new SolidBorder(1));
    }

    // agrega encabezados a una tabla
    private void agregarEncabezadosTabla(Table table, String[] headers) {
        for (String h : headers) {
            table.addHeaderCell(crearCeldaEncabezado(h));
        }
    }

    // ===================== EXPORTAR ASISTENCIA PDF =====================

    @GetMapping("/admin/reportes/asistencia/pdf")
    public void exportarAsistenciaPdf(
            @RequestParam(value = "empleadoId", required = false) Integer empleadoId,
            @RequestParam(value = "desde", required = false) String desdeStr,
            @RequestParam(value = "hasta", required = false) String hastaStr,
            HttpServletResponse response) throws Exception {

        LocalDate desde = parseFecha(desdeStr);
        LocalDate hasta = parseFecha(hastaStr);
        List<Attendance> asistencias = filtrarAsistencias(empleadoId, desde, hasta);

        // Generar nombre de archivo
        String nombreArchivo = "asistencias_";
        if (empleadoId != null) {
            User empleado = userRepository.findById(empleadoId).orElse(null);
            if (empleado != null) {
                nombreArchivo += empleado.getUsername() + "_";
            }
        }
        nombreArchivo += LocalDate.now().toString() + ".pdf";

        configurarRespuestaPdf(response, nombreArchivo);

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        agregarTituloPdf(document, "Reporte de Asistencias");
        agregarFechaPdf(document);

        float[] columnWidths = { 120, 80, 80, 80, 80, 150 };
        Table table = new Table(columnWidths);
        agregarEncabezadosTabla(table,
                new String[] { "Empleado", "Fecha", "Entrada", "Refrigerio", "Salida", "Observaciones" });

        for (Attendance a : asistencias) {
            table.addCell(crearCeldaDato(a.getEmpleado().getUsername()));
            table.addCell(crearCeldaDato(a.getFecha() != null ? a.getFecha().toString() : ""));
            table.addCell(crearCeldaDato(a.getHoraEntrada() != null ? a.getHoraEntrada().toString() : ""));
            table.addCell(crearCeldaDato(a.getHoraRefrigerio() != null ? a.getHoraRefrigerio().toString() : ""));
            table.addCell(crearCeldaDato(a.getHoraSalida() != null ? a.getHoraSalida().toString() : ""));
            table.addCell(crearCeldaDato(a.getObservaciones()));
        }

        table.setMarginTop(10);
        document.add(table);
        document.close();
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
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return "redirect:/admin/inventario/dashboard";
    }

    // vista de asistencia
    @GetMapping("/admin/asistencia")
    public String attendance(HttpSession session, Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;

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
        String r = requireAdmin(session);
        if (r != null)
            return r;
        return "admin/reports";
    }

    // Reportes de inventario
    @GetMapping("/admin/reportes/inventario")
    public String reportesInventario(
            @RequestParam(value = "tipoReporte", required = false, defaultValue = "productos") String tipoReporte,
            @RequestParam(value = "categoriaId", required = false) Integer categoriaId,
            @RequestParam(value = "desde", required = false) String desdeStr,
            @RequestParam(value = "hasta", required = false) String hastaStr,
            HttpSession session, Model model) {
        String r = requireAdmin(session);
        if (r != null)
            return r;

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

        // Obtener datos según el tipo de reporte (usando método auxiliar)
        ReporteInventarioData data = obtenerDatosInventario(tipoReporte, categoriaId, desdeStr, hastaStr);
        model.addAttribute("productos", data.productos);
        model.addAttribute("movimientos", data.movimientos);

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

        // Obtener datos usando el método auxiliar
        ReporteInventarioData data = obtenerDatosInventario(tipoReporte, categoriaId, desdeStr, hastaStr);

        // Configurar respuesta HTTP
        String nombreArchivo = "inventario_" + tipoReporte + "_" + LocalDate.now().toString() + ".pdf";
        configurarRespuestaPdf(response, nombreArchivo);

        // Crear documento PDF
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        agregarTituloPdf(document, obtenerTituloReporte(tipoReporte));
        agregarFechaPdf(document);

        if ("movimientos".equals(tipoReporte) && data.movimientos != null) {
            agregarTablaMovimientos(document, data.movimientos);
        } else if (data.productos != null) {
            agregarTablaProductos(document, data.productos);
        }

        document.close();
    }

    // agrega tabla de movimientos al documento PDF
    private void agregarTablaMovimientos(Document document, List<InventoryMovement> movimientos) {
        float[] columnWidths = { 80, 100, 60, 50, 60, 60, 100, 70 };
        Table table = new Table(columnWidths);
        agregarEncabezadosTabla(table, new String[] { "Fecha", "Producto", "Tipo", "Cantidad", "Stock Ant.",
                "Stock Nuevo", "Motivo", "Usuario" });

        for (InventoryMovement m : movimientos) {
            table.addCell(crearCeldaDato(
                    m.getFechaMovimiento() != null ? m.getFechaMovimiento().toLocalDate().toString() : ""));
            table.addCell(crearCeldaDato(m.getProduct().getNombre()));
            table.addCell(crearCeldaDato(m.getTipoMovimiento().toString()));
            table.addCell(crearCeldaDato(m.getCantidad().toString()));
            table.addCell(crearCeldaDato(m.getStockAnterior().toString()));
            table.addCell(crearCeldaDato(m.getStockNuevo().toString()));
            table.addCell(crearCeldaDato(m.getMotivo()));
            table.addCell(crearCeldaDato(m.getUsuarioResponsable()));
        }
        document.add(table);
    }

    // agrega tabla de productos al documento PDF
    private void agregarTablaProductos(Document document, List<Product> productos) {
        float[] columnWidths = { 120, 80, 60, 60, 70, 70, 80 };
        Table table = new Table(columnWidths);
        agregarEncabezadosTabla(table,
                new String[] { "Producto", "Categoría", "Stock", "Mínimo", "Precio", "Vencimiento", "Ubicación" });

        for (Product p : productos) {
            boolean stockBajo = p.getStockActual().compareTo(p.getStockMinimo()) <= 0;

            Cell cellNombre = crearCeldaDato(p.getNombre());
            if (stockBajo) {
                cellNombre.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            }
            table.addCell(cellNombre);

            table.addCell(crearCeldaDato(p.getCategory() != null ? p.getCategory().getNombre() : ""));
            table.addCell(crearCeldaDato(p.getStockActual() + " " + p.getUnidadMedida()));
            table.addCell(crearCeldaDato(p.getStockMinimo() + " " + p.getUnidadMedida()));
            table.addCell(crearCeldaDato("S/ " + p.getPrecioUnitario()));
            table.addCell(crearCeldaDato(p.getFechaVencimiento() != null ? p.getFechaVencimiento().toString() : ""));
            table.addCell(crearCeldaDato(p.getUbicacion()));
        }
        document.add(table);

        // Resumen al final
        document.add(new Paragraph("\n"));
        BigDecimal valorTotal = inventoryService.getTotalInventoryValue();
        document.add(new Paragraph("Total de productos: " + productos.size()).setFontSize(10));
        document.add(new Paragraph("Valor total del inventario: S/ " + valorTotal).setFontSize(10).setBold());
    }

    // Reportes de asistencia
    @GetMapping("/admin/reportes/asistencia")
    public String reportesAsistencia(
            @RequestParam(value = "empleadoId", required = false) Integer empleadoId,
            @RequestParam(value = "desde", required = false) String desdeStr,
            @RequestParam(value = "hasta", required = false) String hastaStr,
            Model model) {
        List<User> empleados = userRepository.findByRole("USER");

        LocalDate desde = parseFecha(desdeStr);
        LocalDate hasta = parseFecha(hastaStr);
        List<Attendance> asistencias = filtrarAsistencias(empleadoId, desde, hasta);

        model.addAttribute("empleados", empleados);
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("empleadoId", empleadoId);
        model.addAttribute("desde", desdeStr);
        model.addAttribute("hasta", hastaStr);
        return "admin/reportes_asistencia";
    }

    // ===================== CREACIÓN DE USUARIOS =====================

    // método auxiliar para crear usuarios (admin o empleado)
    private String crearUsuario(HttpSession session, String username, String password,
            String role, String redirectSuccess, String redirectError, RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Se requieren nombre de usuario y contraseña");
            return "redirect:" + redirectError;
        }

        String uname = username.trim();
        if (authService.usernameExists(uname)) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe: " + uname);
            return "redirect:" + redirectError;
        }

        java.util.Optional<String> passError = authService.validatePassword(password);
        if (passError.isPresent()) {
            redirectAttributes.addFlashAttribute("error", passError.get());
            return "redirect:" + redirectError;
        }

        String successMsg = role.equals(AuthService.ROLE_ADMIN)
                ? "Admin '" + uname + "' creado exitosamente."
                : "Usuario '" + uname + "' creado exitosamente.";

        return executeAndRedirect(() -> authService.createUser(uname, password, role),
                redirectAttributes, successMsg, redirectSuccess);
    }

    // procesa la creacion de un nuevo empleado
    @PostMapping("/admin/empleados/crear")
    public String handleCreateEmployee(
            HttpSession session,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) {
        return crearUsuario(session, username, password, AuthService.ROLE_USER,
                "/admin/empleados", "/admin/empleados/crear", redirectAttributes);
    }

    // muestra el formulario para editar un empleado existente
    @GetMapping("/admin/empleados/{id}/editar")
    public String showEmployeeEdit(HttpSession session,
            @org.springframework.web.bind.annotation.PathVariable Integer id,
            Model model, RedirectAttributes redirectAttributes) {
        String r = requireAdmin(session);
        if (r != null)
            return r;

        try {
            java.util.Optional<User> u = authService.findById(id);
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
        return crearUsuario(session, username, password, AuthService.ROLE_ADMIN,
                "/admin/crear", "/admin/crear", redirectAttributes);
    }
}

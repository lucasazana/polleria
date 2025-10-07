package com.polleria.polleria.controller;

import com.polleria.polleria.model.Category;
import com.polleria.polleria.model.Product;
import com.polleria.polleria.model.InventoryMovement;
import com.polleria.polleria.service.InventoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/inventario")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // verificar que es admin
    private boolean isAdminSession(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null && "ADMIN".equals(role.toString());
    }

    // dashboard principal del inventario
    @GetMapping("/dashboard")
    public String inventoryDashboard(HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        // estadisticas del dashboard
        model.addAttribute("totalProducts", inventoryService.getActiveProductsCount());
        model.addAttribute("lowStockProducts", inventoryService.getLowStockProductsCount());
        model.addAttribute("totalValue", inventoryService.getTotalInventoryValue());
        model.addAttribute("expiringProducts", inventoryService.getProductsExpiringInDaysCount(30));

        return "admin/inventory/dashboard";
    }

    // === PRODUCTOS ===
    @GetMapping("/productos")
    public String listProducts(@RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String search,
            HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        List<Product> products;
        if (categoryId != null) {
            products = inventoryService.getProductsByCategory(categoryId);
        } else if (search != null && !search.trim().isEmpty()) {
            products = inventoryService.searchProductsByName(search.trim());
        } else {
            products = inventoryService.getAllActiveProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", inventoryService.getAllActiveCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("searchTerm", search);

        return "admin/inventory/products";
    }

    @GetMapping("/productos/crear")
    public String showCreateProductForm(HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        model.addAttribute("categories", inventoryService.getAllActiveCategories());
        return "admin/inventory/product_form";
    }

    @PostMapping("/productos/crear")
    public String createProduct(@RequestParam Integer categoryId,
            @RequestParam String nombre,
            @RequestParam String unidadMedida,
            @RequestParam BigDecimal stockActual,
            @RequestParam BigDecimal stockMinimo,
            @RequestParam BigDecimal precioUnitario,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false) LocalDate fechaCompra,
            @RequestParam(required = false) LocalDate fechaVencimiento,
            @RequestParam(required = false) String ubicacion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        try {
            Optional<Category> categoryOpt = inventoryService.getCategoryById(categoryId);
            if (categoryOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
                return "redirect:/admin/inventario/productos/crear";
            }

            String username = (String) session.getAttribute("username");
            inventoryService.createProduct(categoryOpt.get(), nombre, unidadMedida,
                    stockActual, stockMinimo, precioUnitario,
                    proveedor, fechaCompra, fechaVencimiento, ubicacion);

            redirectAttributes.addFlashAttribute("success", "Producto creado exitosamente");
            return "redirect:/admin/inventario/productos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el producto: " + e.getMessage());
            return "redirect:/admin/inventario/productos/crear";
        }
    }

    @GetMapping("/productos/{id}/editar")
    public String showEditProductForm(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        Optional<Product> productOpt = inventoryService.getProductById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/admin/inventario/productos";
        }

        model.addAttribute("product", productOpt.get());
        model.addAttribute("categories", inventoryService.getAllActiveCategories());
        return "admin/inventory/product_form";
    }

    @PostMapping("/productos/{id}/editar")
    public String updateProduct(@PathVariable Integer id,
            @RequestParam Integer categoryId,
            @RequestParam String nombre,
            @RequestParam String unidadMedida,
            @RequestParam BigDecimal stockMinimo,
            @RequestParam BigDecimal precioUnitario,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false) LocalDate fechaCompra,
            @RequestParam(required = false) LocalDate fechaVencimiento,
            @RequestParam(required = false) String ubicacion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        try {
            Optional<Product> productOpt = inventoryService.getProductById(id);
            Optional<Category> categoryOpt = inventoryService.getCategoryById(categoryId);

            if (productOpt.isEmpty() || categoryOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Producto o categoría no encontrada");
                return "redirect:/admin/inventario/productos";
            }

            Product product = productOpt.get();
            product.setCategory(categoryOpt.get());
            product.setNombre(nombre);
            product.setUnidadMedida(unidadMedida);
            product.setStockMinimo(stockMinimo);
            product.setPrecioUnitario(precioUnitario);
            product.setProveedor(proveedor);
            product.setFechaCompra(fechaCompra);
            product.setFechaVencimiento(fechaVencimiento);
            product.setUbicacion(ubicacion);

            inventoryService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente");
            return "redirect:/admin/inventario/productos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
            return "redirect:/admin/inventario/productos/" + id + "/editar";
        }
    }

    @PostMapping("/productos/{id}/desactivar")
    public String deactivateProduct(@PathVariable Integer id, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        try {
            inventoryService.deactivateProduct(id);
            redirectAttributes.addFlashAttribute("success", "Producto desactivado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al desactivar el producto");
        }

        return "redirect:/admin/inventario/productos";
    }

    // === STOCK ===
    @GetMapping("/productos/{id}/stock")
    public String showUpdateStockForm(@PathVariable Integer id, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: Entrando a showUpdateStockForm con ID: " + id);

        if (!isAdminSession(session)) {
            System.out.println("DEBUG: Usuario no es admin");
            return "redirect:/login";
        }

        try {
            System.out.println("DEBUG: Buscando producto con ID: " + id);
            Optional<Product> productOpt = inventoryService.getProductById(id);

            if (productOpt.isEmpty()) {
                System.out.println("DEBUG: Producto no encontrado con ID: " + id);
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado con ID: " + id);
                return "redirect:/admin/inventario/productos";
            }

            Product product = productOpt.get();
            System.out.println("DEBUG: Producto encontrado: " + product.getNombre());
            model.addAttribute("product", product);

            // Obtener movimientos recientes del producto
            System.out.println("DEBUG: Obteniendo movimientos recientes");
            List<InventoryMovement> recentMovements = inventoryService.getRecentMovementsByProduct(id);
            System.out.println(
                    "DEBUG: Movimientos encontrados: " + (recentMovements != null ? recentMovements.size() : 0));
            model.addAttribute("recentMovements", recentMovements);

            System.out.println("DEBUG: Retornando vista admin/inventory/update_stock");
            return "admin/inventory/update_stock";

        } catch (Exception e) {
            System.err.println("ERROR: Excepción en showUpdateStockForm: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/inventario/productos";
        }
    }

    @PostMapping("/productos/{id}/stock")
    public String updateStock(@PathVariable Integer id,
            @RequestParam String tipoMovimiento,
            @RequestParam BigDecimal cantidad,
            @RequestParam String motivo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        try {
            String username = (String) session.getAttribute("username");

            // Validaciones básicas
            if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor a cero");
                return "redirect:/admin/inventario/productos/" + id + "/stock";
            }

            if (motivo == null || motivo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe especificar un motivo");
                return "redirect:/admin/inventario/productos/" + id + "/stock";
            }

            // Actualizar el stock usando el nuevo método del servicio
            inventoryService.updateStock(id, tipoMovimiento, cantidad, motivo, username);
            redirectAttributes.addFlashAttribute("success", "Stock actualizado exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/admin/inventario/productos/" + id + "/stock";
        }

        return "redirect:/admin/inventario/productos";
    }

    // === MOVIMIENTOS ===
    @GetMapping("/movimientos")
    public String listMovements(@RequestParam(required = false) Integer productId,
            HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        List<InventoryMovement> movements;
        if (productId != null) {
            movements = inventoryService.getMovementsByProduct(productId);
            Optional<Product> product = inventoryService.getProductById(productId);
            model.addAttribute("selectedProduct", product.orElse(null));
        } else {
            movements = inventoryService.getRecentMovements();
        }

        model.addAttribute("movements", movements);
        model.addAttribute("products", inventoryService.getAllActiveProducts());
        return "admin/inventory/movements";
    }

    // === ALERTAS ===
    @GetMapping("/alertas")
    public String showAlerts(HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        model.addAttribute("lowStockProducts", inventoryService.getProductsWithLowStock());
        model.addAttribute("expiringProducts",
                inventoryService.getProductsExpiringBefore(LocalDate.now().plusDays(30)));

        return "admin/inventory/alerts";
    }

    // === CATEGORÍAS ===
    @GetMapping("/categorias")
    public String listCategories(HttpSession session, Model model) {
        if (!isAdminSession(session)) {
            return "redirect:/login";
        }

        model.addAttribute("categories", inventoryService.getAllCategories());
        return "admin/inventory/categories";
    }
}
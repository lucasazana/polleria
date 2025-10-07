package com.polleria.polleria.service;

import com.polleria.polleria.model.Category;
import com.polleria.polleria.model.Product;
import com.polleria.polleria.model.InventoryMovement;
import com.polleria.polleria.repository.CategoryRepository;
import com.polleria.polleria.repository.ProductRepository;
import com.polleria.polleria.repository.InventoryMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    public InventoryService(CategoryRepository categoryRepository,
            ProductRepository productRepository,
            InventoryMovementRepository movementRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    // === CATEGORIA METHODS ===
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findActiveCategoriesOrderByName();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category createCategory(String nombre, String descripcion) {
        Category category = new Category(nombre, descripcion);
        return categoryRepository.save(category);
    }

    public void deleteCategoryById(Integer id) {
        categoryRepository.deleteById(id);
    }

    // === PRODUCT METHODS ===
    public List<Product> getAllActiveProducts() {
        return productRepository.findByEstadoTrue();
    }

    public List<Product> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategoryIdAndEstadoTrue(categoryId);
    }

    public List<Product> getProductsWithLowStock() {
        return productRepository.findProductsWithLowStock();
    }

    public List<Product> getProductsExpiringBefore(LocalDate fecha) {
        return productRepository.findProductsExpiringBefore(fecha);
    }

    public List<Product> searchProductsByName(String nombre) {
        return productRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product createProduct(Category category, String nombre, String unidadMedida,
            BigDecimal stockActual, BigDecimal stockMinimo, BigDecimal precioUnitario,
            String proveedor, LocalDate fechaCompra, LocalDate fechaVencimiento, String ubicacion) {
        Product product = new Product(category, nombre, unidadMedida, stockActual, stockMinimo, precioUnitario);
        product.setProveedor(proveedor);
        product.setFechaCompra(fechaCompra);
        product.setFechaVencimiento(fechaVencimiento);
        product.setUbicacion(ubicacion);

        Product savedProduct = productRepository.save(product);

        // registrar movimiento inicial
        registerMovement(savedProduct, InventoryMovement.MovementType.ENTRADA,
                stockActual, BigDecimal.ZERO, stockActual,
                "Stock inicial", "Sistema");

        return savedProduct;
    }

    public void deactivateProduct(Integer id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setEstado(false);
            productRepository.save(product);
        }
    }

    // === INVENTORY MOVEMENT METHODS ===
    public InventoryMovement registerMovement(Product product, InventoryMovement.MovementType tipo,
            BigDecimal cantidad, BigDecimal stockAnterior, BigDecimal stockNuevo,
            String motivo, String usuarioResponsable) {
        InventoryMovement movement = new InventoryMovement(product, tipo, cantidad, stockAnterior, stockNuevo, motivo,
                usuarioResponsable);
        return movementRepository.save(movement);
    }

    public void updateStock(Integer productId, String tipoMovimiento, BigDecimal cantidad, String motivo,
            String usuarioResponsable) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + productId);
        }

        Product product = productOpt.get();
        BigDecimal stockAnterior = product.getStockActual();
        BigDecimal stockNuevo;

        // Usar el nuevo método fromString que es tolerante a mayúsculas/minúsculas
        InventoryMovement.MovementType movementType = InventoryMovement.MovementType.fromString(tipoMovimiento);

        // Validar y procesar según el tipo de movimiento
        switch (movementType) {
            case ENTRADA:
                stockNuevo = stockAnterior.add(cantidad);
                break;
            case SALIDA:
                stockNuevo = stockAnterior.subtract(cantidad);
                if (stockNuevo.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("Stock insuficiente. Stock actual: " + stockAnterior
                            + ", Cantidad solicitada: " + cantidad);
                }
                break;
            case AJUSTE:
                stockNuevo = cantidad;
                if (stockNuevo.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("El stock no puede ser negativo");
                }
                break;
            default:
                throw new RuntimeException("Tipo de movimiento no válido: " + tipoMovimiento);
        }

        // Actualizar el producto
        product.setStockActual(stockNuevo);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        // Registrar el movimiento
        registerMovement(product, movementType, cantidad, stockAnterior, stockNuevo, motivo, usuarioResponsable);
    }

    public List<InventoryMovement> getMovementsByProduct(Integer productId) {
        return movementRepository.findByProductIdOrderByFechaMovimientoDesc(productId);
    }

    public List<InventoryMovement> getMovementsByDateRange(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movementRepository.findMovementsByDateRange(fechaInicio, fechaFin);
    }

    public List<InventoryMovement> getRecentMovements() {
        return movementRepository.findRecentMovements();
    }

    public List<InventoryMovement> getRecentMovementsByProduct(Integer productId) {
        return movementRepository.findByProductIdOrderByFechaMovimientoDesc(productId);
    }

    // === DASHBOARD METHODS ===
    public BigDecimal getTotalInventoryValue() {
        BigDecimal total = productRepository.getTotalInventoryValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    public long getActiveProductsCount() {
        return productRepository.findByEstadoTrue().size();
    }

    public long getLowStockProductsCount() {
        return productRepository.findProductsWithLowStock().size();
    }

    public long getProductsExpiringInDaysCount(int days) {
        LocalDate fecha = LocalDate.now().plusDays(days);
        return productRepository.findProductsExpiringBefore(fecha).size();
    }

    // === UTILITY METHODS ===
    public void initializeDefaultCategories() {
        String[] defaultCategories = { "Carnes", "Guarniciones", "Condimentos", "Verduras", "Aceites", "Bebidas" };
        String[] descriptions = {
                "Productos cárnicos y proteínas",
                "Acompañamientos y guarniciones",
                "Especias, salsas y condimentos",
                "Verduras frescas y vegetales",
                "Aceites y grasas de cocina",
                "Bebidas y líquidos"
        };

        for (int i = 0; i < defaultCategories.length; i++) {
            Optional<Category> existing = categoryRepository.findByNombre(defaultCategories[i]);
            if (existing.isEmpty()) {
                createCategory(defaultCategories[i], descriptions[i]);
            }
        }
    }
}
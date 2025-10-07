package com.polleria.polleria.repository;

import com.polleria.polleria.model.Product;
import com.polleria.polleria.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // buscar por nombre
    Optional<Product> findByNombre(String nombre);

    // buscar productos activos
    List<Product> findByEstadoTrue();

    // buscar por categoria
    List<Product> findByCategoryAndEstadoTrue(Category category);

    // buscar por categoria id
    List<Product> findByCategoryIdAndEstadoTrue(Integer categoryId);

    // productos con stock bajo
    @Query("SELECT p FROM Product p WHERE p.stockActual <= p.stockMinimo AND p.estado = true")
    List<Product> findProductsWithLowStock();

    // productos por proveedor
    List<Product> findByProveedorAndEstadoTrue(String proveedor);

    // productos que vencen pronto
    @Query("SELECT p FROM Product p WHERE p.fechaVencimiento <= :fecha AND p.estado = true")
    List<Product> findProductsExpiringBefore(@Param("fecha") LocalDate fecha);

    // buscar por nombre que contenga texto
    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.estado = true")
    List<Product> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    // valor total del inventario
    @Query("SELECT SUM(p.stockActual * p.precioUnitario) FROM Product p WHERE p.estado = true")
    BigDecimal getTotalInventoryValue();

    // valor total por categoria
    @Query("SELECT SUM(p.stockActual * p.precioUnitario) FROM Product p WHERE p.category.id = :categoryId AND p.estado = true")
    BigDecimal getTotalValueByCategory(@Param("categoryId") Integer categoryId);

    // productos con stock disponible
    @Query("SELECT p FROM Product p WHERE p.stockActual > 0 AND p.estado = true")
    List<Product> findProductsWithStock();

    // productos por ubicacion
    List<Product> findByUbicacionAndEstadoTrue(String ubicacion);
}
package com.polleria.polleria.repository;

import com.polleria.polleria.model.InventoryMovement;
import com.polleria.polleria.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Integer> {

    // movimientos por producto
    List<InventoryMovement> findByProductOrderByFechaMovimientoDesc(Product product);

    // movimientos por producto id
    List<InventoryMovement> findByProductIdOrderByFechaMovimientoDesc(Integer productId);

    // movimientos por tipo
    List<InventoryMovement> findByTipoMovimientoOrderByFechaMovimientoDesc(InventoryMovement.MovementType tipo);

    // movimientos por rango de fechas
    @Query("SELECT m FROM InventoryMovement m WHERE m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaMovimiento DESC")
    List<InventoryMovement> findMovementsByDateRange(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // movimientos por usuario responsable
    List<InventoryMovement> findByUsuarioResponsableOrderByFechaMovimientoDesc(String usuarioResponsable);

    // ultimos movimientos
    @Query("SELECT m FROM InventoryMovement m ORDER BY m.fechaMovimiento DESC")
    List<InventoryMovement> findRecentMovements();

    // movimientos de un producto en un rango de fechas
    @Query("SELECT m FROM InventoryMovement m WHERE m.product.id = :productId " +
            "AND m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY m.fechaMovimiento DESC")
    List<InventoryMovement> findByProductIdAndDateRange(@Param("productId") Integer productId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // movimientos por categoria de producto
    @Query("SELECT m FROM InventoryMovement m WHERE m.product.category.id = :categoryId ORDER BY m.fechaMovimiento DESC")
    List<InventoryMovement> findByCategoryId(@Param("categoryId") Integer categoryId);
}
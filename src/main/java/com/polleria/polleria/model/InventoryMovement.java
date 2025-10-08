
package com.polleria.polleria.model;

import com.polleria.polleria.converter.MovementTypeConverter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos_inventario")
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product product;

    @Convert(converter = MovementTypeConverter.class)
    @Column(name = "tipo_movimiento", nullable = false)
    private MovementType tipoMovimiento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "stock_anterior", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockAnterior;

    @Column(name = "stock_nuevo", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockNuevo;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "usuario_responsable")
    private String usuarioResponsable;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // enum para tipos de movimiento
    public enum MovementType {
        ENTRADA("entrada"),
        SALIDA("salida"),
        AJUSTE("ajuste");

        private final String value;

        MovementType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        // Método para convertir string a enum de forma tolerante
        public static MovementType fromString(String value) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Movement type cannot be null or empty");
            }

            String normalizedValue = value.trim().toUpperCase();

            switch (normalizedValue) {
                case "ENTRADA":
                case "entrada":
                    return ENTRADA;
                case "SALIDA":
                case "salida":
                    return SALIDA;
                case "AJUSTE":
                case "ajuste":
                    return AJUSTE;
                default:
                    throw new IllegalArgumentException("Unknown movement type: " + value);
            }
        }
    }

    // metodos de utilidad
    public String getTipoMovimientoDisplayName() {
        switch (tipoMovimiento) {
            case ENTRADA:
                return "Entrada";
            case SALIDA:
                return "Salida";
            case AJUSTE:
                return "Ajuste";
            default:
                return tipoMovimiento.toString();
        }
    }

    public String getFechaMovimientoFormatted() {
        if (fechaMovimiento == null) {
            return "";
        }
        return String.format("%02d/%02d/%d %02d:%02d",
                fechaMovimiento.getDayOfMonth(),
                fechaMovimiento.getMonthValue(),
                fechaMovimiento.getYear(),
                fechaMovimiento.getHour(),
                fechaMovimiento.getMinute());
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
package com.polleria.polleria.converter;

import com.polleria.polleria.model.InventoryMovement;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MovementTypeConverter implements AttributeConverter<InventoryMovement.MovementType, String> {

    @Override
    public String convertToDatabaseColumn(InventoryMovement.MovementType attribute) {
        if (attribute == null) {
            return null;
        }
        // Siempre guardar en mayúsculas en la base de datos
        return attribute.name();
    }

    @Override
    public InventoryMovement.MovementType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        // Usar nuestro método fromString que es tolerante
        try {
            return InventoryMovement.MovementType.fromString(dbData);
        } catch (IllegalArgumentException e) {
            // Si falla, intentar con valueOf directo como fallback
            return InventoryMovement.MovementType.valueOf(dbData.toUpperCase());
        }
    }
}
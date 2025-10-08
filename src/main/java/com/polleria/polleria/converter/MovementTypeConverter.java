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
        // guardar en mayusculas en la base de datos
        return attribute.name();
    }

    @Override
    public InventoryMovement.MovementType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        // usar metodo fromString
        try {
            return InventoryMovement.MovementType.fromString(dbData);
        } catch (IllegalArgumentException e) {
            // si falla intenta con valueof directo como fallback
            return InventoryMovement.MovementType.valueOf(dbData.toUpperCase());
        }
    }
}
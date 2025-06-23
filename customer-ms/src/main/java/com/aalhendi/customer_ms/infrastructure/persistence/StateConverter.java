package com.aalhendi.customer_ms.infrastructure.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

/**
 * Converter for soft delete state.
 * TODO(aalhendi): move to a common lib!
 */
@Converter(autoApply = true)
public class StateConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return attribute ? 1 : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        if (Objects.isNull(dbData)) {
            return false;
        }
        return dbData == 1;
    }
}
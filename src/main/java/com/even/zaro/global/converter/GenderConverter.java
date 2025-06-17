package com.even.zaro.global.converter;

import com.even.zaro.entity.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        return gender != null ? gender.name() : null;
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        return dbData != null ? Gender.valueOf(dbData) : null;
    }
}

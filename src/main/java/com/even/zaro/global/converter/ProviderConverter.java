package com.even.zaro.global.converter;

import com.even.zaro.entity.Provider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProviderConverter implements AttributeConverter<Provider, String> {

    @Override
    public String convertToDatabaseColumn(Provider provider) {
        return provider != null ? provider.name() : null;
    }

    @Override
    public Provider convertToEntityAttribute(String dbData) {
        return dbData != null ? Provider.valueOf(dbData) : null;
    }
}

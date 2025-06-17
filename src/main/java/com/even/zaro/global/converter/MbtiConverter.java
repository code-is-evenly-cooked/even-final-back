package com.even.zaro.global.converter;

import com.even.zaro.entity.Mbti;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MbtiConverter implements AttributeConverter<Mbti, String> {

    @Override
    public String convertToDatabaseColumn(Mbti mbti) {
        return mbti != null ? mbti.name() : null;
    }

    @Override
    public Mbti convertToEntityAttribute(String dbData) {
        return dbData != null ? Mbti.valueOf(dbData) : null;
    }
}

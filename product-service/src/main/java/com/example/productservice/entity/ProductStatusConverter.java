package com.example.productservice.entity;

import jakarta.persistence.AttributeConverter;

public class ProductStatusConverter implements AttributeConverter<ProductStatus, String > {
    @Override
    public String convertToDatabaseColumn(ProductStatus attribute) {
        return attribute.getLegacyCode();
    }

    @Override
    public ProductStatus convertToEntityAttribute(String dbData) {
        return ProductStatus.ofLegacyCode(dbData);
    }
}

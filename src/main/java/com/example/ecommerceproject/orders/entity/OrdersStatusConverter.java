package com.example.ecommerceproject.orders.entity;

import jakarta.persistence.AttributeConverter;

public class OrdersStatusConverter implements AttributeConverter<OrdersStatus, String > {
    @Override
    public String convertToDatabaseColumn(OrdersStatus attribute) {
        return attribute.getLegacyCode();
    }

    @Override
    public OrdersStatus convertToEntityAttribute(String dbData) {
        return OrdersStatus.ofLegacyCode(dbData);
    }
}

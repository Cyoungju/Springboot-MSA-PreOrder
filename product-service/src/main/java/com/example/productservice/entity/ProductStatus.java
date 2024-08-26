package com.example.productservice.entity;

import com.example.productservice.core.exception.CustomException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductStatus {
    AVAILABLE("판매중","1"),
    DISCONTINUED("판매중지", "2"),
    OUT_OF_STOCK("재고없음", "3") ;

    private String desc;
    private String legacyCode;

    ProductStatus(String desc, String legacyCode) {
        this.desc = desc;
        this.legacyCode = legacyCode;
    }

    public static ProductStatus ofLegacyCode(String legacyCode){
        return Arrays.stream(ProductStatus.values())
                .filter(v -> v.getLegacyCode().equals(legacyCode))
                .findAny()
                .orElseThrow(() -> new CustomException("legacyCode=[%s] 가 존재하지 않습니다."));
    }
}


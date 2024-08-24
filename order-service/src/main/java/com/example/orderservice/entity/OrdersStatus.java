package com.example.orderservice.entity;

import com.example.orderservice.core.exception.CustomException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrdersStatus {
    ACCEPTED_FAILED("결제실패", "0"),
    ACCEPTED("결제완료", "1"),
    ON_DELIVERY("배달중", "2"),
    SHIPPED("배달완료", "3"),
    CANCELED("취소", "4"),
    CONFIRMED("확정", "5"),
    RETURN_REQUESTED("반품 진행중", "6"),
    RETURNED("반품완료", "7");


    private String desc;
    private String legacyCode;


    OrdersStatus(String desc, String legacyCode) {
        this.desc = desc;
        this.legacyCode = legacyCode;
    }


    public static OrdersStatus ofLegacyCode(String legacyCode){
        return Arrays.stream(OrdersStatus.values())
                .filter(v -> v.getLegacyCode().equals(legacyCode))
                .findAny()
                .orElseThrow(() -> new CustomException("legacyCode=[%s] 가 존재하지 않습니다."));
    }
}

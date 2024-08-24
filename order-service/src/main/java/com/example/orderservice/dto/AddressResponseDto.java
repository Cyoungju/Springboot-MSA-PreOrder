package com.example.orderservice.dto;

import com.example.orderservice.core.utils.EncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {
    private String addressName;

    private String address;

    private String detailAdr;

    private String phone;

}

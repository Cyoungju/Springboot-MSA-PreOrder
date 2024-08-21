package com.example.userservice.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressAddDto {
    private String addressName;

    private String address;

    private String detailAdr;

    private String phone;

    private boolean defaultAdr;
}

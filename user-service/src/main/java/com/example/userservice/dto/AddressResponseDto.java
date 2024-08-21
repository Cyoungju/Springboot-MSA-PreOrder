package com.example.userservice.dto;

import com.example.userservice.entity.Address;
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

    public AddressResponseDto(Address address) {
        this.address = address.getAddress();
        this.detailAdr = address.getDetailAdr();
        this.addressName = address.getAddressName();
        this.phone = address.getPhone();
    }

}

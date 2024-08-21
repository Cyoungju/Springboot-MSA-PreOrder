package com.example.userservice.service;

import com.example.userservice.dto.AddressAddDto;
import com.example.userservice.dto.AddressResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface AddressService {

    void addAddress(AddressAddDto addressAddDto, String email);
    AddressResponseDto getDefaultAddress(Long memberId);
    AddressResponseDto getAddressById(Long addressId);
}

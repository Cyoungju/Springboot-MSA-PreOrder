package com.example.userservice.client;

import com.example.userservice.dto.AddressResponseDto;
import com.example.userservice.dto.MemberResponseDto;
import com.example.userservice.service.AddressService;
import com.example.userservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client")
public class MemberControllerClient {

    private final AddressService addressService;

    @GetMapping("/defaultAddress/{memberEmail}")
    public AddressResponseDto getDefaultAddress(@PathVariable("memberEmail") String email) {
        return addressService.getDefaultAddress(email);
    }

    @GetMapping("/address/{addressId}")
    public AddressResponseDto getAddressById(@PathVariable Long addressId) {
        return addressService.getAddressById(addressId);
    }
}

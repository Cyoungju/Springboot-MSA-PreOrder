package com.example.orderservice.client;


import com.example.orderservice.dto.AddressResponseDto;
import com.example.orderservice.dto.MemberResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "user-service")
public interface MemberServiceClient {
    @GetMapping("/api/client/{email}")
    MemberResponseDto getUserByEmail(@PathVariable("email") String email);

    @GetMapping("/api/client/defaultAddress/{memberId}")
    Optional<AddressResponseDto> getDefaultAddress(@PathVariable("memberId") Long memberId);

    @GetMapping("/api/client/address/{addressId}")
    Optional<AddressResponseDto> getAddressById(@PathVariable("addressId") Long addressId);
}

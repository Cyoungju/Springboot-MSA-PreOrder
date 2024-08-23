package com.example.orderservice.client;

import com.example.orderservice.dto.AddressResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "user-service")
public interface MemberServiceClient {

    @GetMapping("/api/client/defaultAddress/{memberEmail}")
    Optional<AddressResponseDto> getDefaultAddress(@PathVariable("memberEmail") String email);

    @GetMapping("/api/client/address/{addressId}")
    Optional<AddressResponseDto> getAddressById(@PathVariable("addressId") Long addressId);

}

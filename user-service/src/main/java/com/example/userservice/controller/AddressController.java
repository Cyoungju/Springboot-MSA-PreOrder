package com.example.userservice.controller;


import com.example.userservice.core.exception.CustomException;
import com.example.userservice.core.utils.ApiUtils;
import com.example.userservice.dto.AddressAddDto;
import com.example.userservice.dto.AddressResponseDto;
import com.example.userservice.dto.MemberDto;
import com.example.userservice.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/address")
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<?> addAddress(@RequestBody @Valid AddressAddDto addressAddDto, @RequestHeader("X-Authenticated-User") String email) {
        addressService.addAddress(addressAddDto, email);
        return ResponseEntity.ok( ApiUtils.success("배송지가 추가 되었습니다!") );
    }


}

package com.example.orderservice.client;


import com.example.orderservice.dto.MemberResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface MemberServiceClient {
    @GetMapping("/users/{email}")
    MemberResponseDto getUserByEmail(@PathVariable("email") String email);
}

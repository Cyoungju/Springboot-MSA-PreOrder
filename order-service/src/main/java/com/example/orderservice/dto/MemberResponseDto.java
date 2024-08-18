package com.example.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberResponseDto {
    private Long id;
    private String email;
    private String username;
    private String role;
}

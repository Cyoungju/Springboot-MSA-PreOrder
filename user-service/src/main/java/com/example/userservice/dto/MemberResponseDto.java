package com.example.userservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class MemberResponseDto {
    private Long id;
    private String email;
    private String username;
}
package com.example.ecommerceproject.member.dto;

import com.example.ecommerceproject.member.entity.MemberRole;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    @NotEmpty
    @Pattern(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식으로 작성해주세요")
    private String email;

    @NotEmpty
    @Size(min = 8, max = 20, message = "8자 이상 20자 이내로 작성 가능합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!~`<>,./?;:'\"\\[\\]{}\\\\()|_-])\\S*$", message = "영문, 숫자, 특수문자가 포함되어야하고 공백이 포함될 수 없습니다.")
    private String password;

    @NotEmpty
    private String username;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String address;

    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();


//    @Builder
//    public Member toEntity() {
//        Member member = Member.builder()
//                .email(EncryptionUtil.encrypt(email))
//                .password(password)
//                .username(EncryptionUtil.encrypt(username))
//                .phone(EncryptionUtil.encrypt(phone))
//                .address(EncryptionUtil.encrypt(address))
//                .build();
//
//        // 기본 사용자 - defalt
//        member.addRole(MemberRole.USER);
//
//        return member;
//    }
}

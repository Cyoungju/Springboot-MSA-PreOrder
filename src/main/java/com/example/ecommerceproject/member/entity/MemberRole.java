package com.example.ecommerceproject.member.entity;

import lombok.Getter;

@Getter
public enum MemberRole {
    USER(Authority.USER),  // 사용자 권한
    MANAGER(Authority.MANAGER),// 매니저 권한
    ADMIN(Authority.ADMIN);  // 관리자 권한

    private final String authority;

    MemberRole(String authority) {
        this.authority = authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String MANAGER = "ROLE_MANAGER";

    }
}

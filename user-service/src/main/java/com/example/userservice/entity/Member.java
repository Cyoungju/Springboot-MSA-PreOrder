package com.example.userservice.entity;

import com.example.userservice.core.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString(exclude = "memberRoleList")
@Entity
@Builder
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 45, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String phone;

    @Column(length = 300, nullable = false)
    private String address;

    @Column(length = 300)
    private String detailAdr;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="member_role_list")
    @Enumerated(EnumType.STRING) // 열거형의 값을 문자열로 저장
    @Builder.Default // 처음부터 사용할수 있도록 초기화 -> null값이 나오지 않게 하기 위해
    private List<MemberRole> memberRoleList = new ArrayList<>();

    public void addRole(MemberRole memberRole){
        memberRoleList.add(memberRole);
    }

    public void clearRole(){
        memberRoleList.clear();
    }

    @Builder
    public Member(Long id, String email, String password, String username, String phone, String address,String detailAdr, List<MemberRole> memberRoleList) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
        this.address = address;
        this.detailAdr = detailAdr;
        this.memberRoleList = memberRoleList;
    }

}

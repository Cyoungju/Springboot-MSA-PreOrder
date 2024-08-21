package com.example.userservice.entity;

import com.example.userservice.core.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "address")
public class Address extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 300, nullable = false)
    private String addressName;

    @Column(length = 300, nullable = false)
    private String address;

    @Column(length = 300)
    private String detailAdr;

    @Column(length = 100)
    private String phone;

    private boolean defaultAdr;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}

package com.example.paymentservice.entity;


import com.example.paymentservice.core.utils.BaseTimeEntity;
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
@Table(name="payment")
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean paymentStatus;

    private Long orderId;

    private Long totalPrice;


    public void changePaymentStatus(boolean paymentStatus){
        this.paymentStatus = paymentStatus;
    }

}

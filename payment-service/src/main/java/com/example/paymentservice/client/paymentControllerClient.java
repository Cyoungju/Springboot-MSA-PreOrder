package com.example.paymentservice.client;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client")
public class paymentControllerClient {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public boolean purchase(@RequestBody PaymentRequest paymentRequest){
        return paymentService.processPayment(paymentRequest);
    }

}

package com.example.it211ss10hw02.controller;

import com.example.it211ss10hw02.model.dto.request.PaymentRequest;
import com.example.it211ss10hw02.model.dto.response.ApiDataResponse;
import com.example.it211ss10hw02.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<ApiDataResponse> pay(@RequestBody PaymentRequest request) {
        ApiDataResponse response = paymentService.pay(
                request.getOrderId(),
                request.getUserId(),
                request.getAmount()
        );
        return ResponseEntity.ok(response);
    }
}

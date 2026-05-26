package com.example.it211ss10hw02.service;

import com.example.it211ss10hw02.model.dto.response.ApiDataResponse;

public interface PaymentService {
    ApiDataResponse pay(Long orderId, Long userId, Double amount);
}

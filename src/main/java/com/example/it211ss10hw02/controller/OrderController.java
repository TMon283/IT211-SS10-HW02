package com.example.it211ss10hw02.controller;

import com.example.it211ss10hw02.exception.BusinessException;
import com.example.it211ss10hw02.model.dto.request.OrderRequest;
import com.example.it211ss10hw02.model.dto.response.ApiDataResponse;
import com.example.it211ss10hw02.model.entity.Order;
import com.example.it211ss10hw02.model.entity.User;
import com.example.it211ss10hw02.repository.OrderRepository;
import com.example.it211ss10hw02.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final UserRepository userRepo;
    private final OrderRepository orderRepo;

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public ResponseEntity<ApiDataResponse<Order>> createOrder(@RequestBody OrderRequest request) {
        try {
            User user = userRepo.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy user"));

            Order order = new Order();
            order.setUser(user);
            order.setTotalAmount(request.getTotalAmount());
            order.setStatus("PENDING");

            orderRepo.save(order);

            log.info("Tạo order thành công cho User {} với amount {}", user.getId(), request.getTotalAmount());
            return ResponseEntity.ok(
                    ApiDataResponse.<Order>builder()
                            .success(true)
                            .message("Tạo order thành công")
                            .data(order)
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (BusinessException e) {
            log.warn("Lỗi nghiệp vụ khi tạo order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiDataResponse.<Order>builder()
                            .success(false)
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tạo order", e);
            return ResponseEntity.internalServerError().body(
                    ApiDataResponse.<Order>builder()
                            .success(false)
                            .message("Lỗi hệ thống")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build()
            );
        }
    }
}

package com.example.it211ss10hw02.service.impl;

import com.example.it211ss10hw02.exception.BusinessException;
import com.example.it211ss10hw02.model.dto.response.ApiDataResponse;
import com.example.it211ss10hw02.model.entity.Order;
import com.example.it211ss10hw02.model.entity.User;
import com.example.it211ss10hw02.model.entity.UserAccount;
import com.example.it211ss10hw02.repository.OrderRepository;
import com.example.it211ss10hw02.repository.UserAccountRepository;
import com.example.it211ss10hw02.repository.UserRepository;
import com.example.it211ss10hw02.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final UserAccountRepository accountRepo;


    @Transactional
    @Override
    public ApiDataResponse pay(Long orderId, Long userId, Double amount) {
        try {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy user"));
            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy order"));

            if (!order.getUser().getId().equals(userId)) {
                log.warn("Order {} không thuộc về User {}", orderId, userId);
                throw new BusinessException("Order không thuộc về user");
            }

            if ("PAID".equals(order.getStatus())) {
                log.warn("Order {} đã được thanh toán trước đó", orderId);
                throw new BusinessException("Order đã PAID");
            }

            if (amount == 9999) {
                throw new RuntimeException("Đứt kết nối DB");
            }

            UserAccount account = accountRepo.findByUser(user)
                    .orElseThrow(() -> new BusinessException("Không tìm thấy account"));

            if (account.getBalance() < amount) {
                log.warn("User {} không đủ tiền. Balance={}, Amount={}", userId, account.getBalance(), amount);
                throw new BusinessException("Không đủ tiền");
            }

            account.setBalance(account.getBalance() - amount);
            order.setStatus("PAID");
            accountRepo.save(account);
            orderRepo.save(order);

            log.info("Thanh toán thành công. User={}, Order={}, Amount={}", userId, orderId, amount);
            return ApiDataResponse.builder()
                    .success(true)
                    .message("Thanh toán thành công")
                    .data(order)
                    .status(HttpStatus.OK)
                    .build();

        } catch (BusinessException e) {
            log.warn("Lỗi nghiệp vụ: {}", e.getMessage());
            return ApiDataResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi thanh toán orderId=" + orderId, e);
            return ApiDataResponse.builder()
                    .success(false)
                    .message("Lỗi hệ thống")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}

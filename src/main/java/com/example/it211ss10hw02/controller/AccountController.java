package com.example.it211ss10hw02.controller;

import com.example.it211ss10hw02.exception.BusinessException;
import com.example.it211ss10hw02.model.dto.request.AccountRequest;
import com.example.it211ss10hw02.model.dto.response.ApiDataResponse;
import com.example.it211ss10hw02.model.entity.User;
import com.example.it211ss10hw02.model.entity.UserAccount;
import com.example.it211ss10hw02.repository.UserAccountRepository;
import com.example.it211ss10hw02.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final UserRepository userRepo;
    private final UserAccountRepository accountRepo;

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @PostMapping
    public ResponseEntity<ApiDataResponse<UserAccount>> createAccount(@RequestBody AccountRequest request) {
        try {
            User user = userRepo.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy user"));

            UserAccount account = new UserAccount();
            account.setUser(user);
            account.setBalance(request.getBalance());

            accountRepo.save(account);

            log.info("Tạo tài khoản thành công cho User {} với balance {}", user.getId(), request.getBalance());
            return ResponseEntity.ok(
                    ApiDataResponse.<UserAccount>builder()
                            .success(true)
                            .message("Tạo tài khoản thành công")
                            .data(account)
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (BusinessException e) {
            log.warn("Lỗi nghiệp vụ khi tạo account: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiDataResponse.<UserAccount>builder()
                            .success(false)
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tạo account", e);
            return ResponseEntity.internalServerError().body(
                    ApiDataResponse.<UserAccount>builder()
                            .success(false)
                            .message("Lỗi hệ thống")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build()
            );
        }
    }
}

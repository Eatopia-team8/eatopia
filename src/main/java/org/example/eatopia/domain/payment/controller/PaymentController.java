package org.example.eatopia.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.auth.dto.AuthUser;
import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;
import org.example.eatopia.domain.payment.service.command.PaymentCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentCommandService paymentCommandService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid PaymentCreateRequest request
    ) {
        Long userId = authUser.getId();

        PaymentResponse createdPayment = paymentCommandService.createPayment(userId, request);
        return ResponseEntity.ok(createdPayment);
    }
}

package org.example.eatopia.domain.payment.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentUpdateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentVerifyRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;
import org.example.eatopia.domain.payment.service.command.PaymentCommandService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentCommandService paymentCommandService;

    @PreAuthorize("hasRole('ROLE_BUYER')")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @AuthenticationPrincipal UserPrincipal authUser,
            @RequestBody @Valid PaymentCreateRequest request
    ) {
        Long userId = authUser.getId();

        PaymentResponse createdPayment = paymentCommandService.createPayment(userId, request);
        return ResponseEntity.ok(createdPayment);
    }

    @PreAuthorize("hasRole('ROLE_BUYER')")
    @PatchMapping("/{paymentId}/method")
    public ResponseEntity<PaymentResponse> updatePaymentMethod(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PathVariable Long paymentId,
            @RequestBody @Valid PaymentUpdateRequest request
    ) {
        PaymentResponse updatedPayment = paymentCommandService.updatePaymentMethod(authUser.getId(), paymentId, request);
        return ResponseEntity.ok(updatedPayment);
    }

    @PreAuthorize("hasRole('ROLE_BUYER')")
    @PostMapping("/verify")
    public ResponseEntity<Response<PaymentResponse>> verifyPayment(
            @AuthenticationPrincipal UserPrincipal authUser,
            @RequestBody @Valid PaymentVerifyRequest request
    ) throws IamportResponseException, IOException {

        PaymentResponse verifiedPayment = paymentCommandService.verifyPayment(authUser.getId(), request);

        return ResponseEntity.ok(Response.success(verifiedPayment));
    }
}

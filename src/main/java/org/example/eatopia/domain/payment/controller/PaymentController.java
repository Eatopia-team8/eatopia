package org.example.eatopia.domain.payment.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Payment API", description = "결제 관련 API")
@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentCommandService paymentCommandService;

    @Operation(summary = "결제 생성", description = "주문에 대한 결제 정보를 생성합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "생성 성공"),
                    @ApiResponse(responseCode = "400", description = "생성 실패")
            })
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

    @Operation(summary = "결제 수단 변경", description = "결제 수단을 변경합니다.(pending 일때 가능)",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "변경 성공"),
                    @ApiResponse(responseCode = "400", description = "변경 실패")
            })
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

    @Operation(summary = "결제 검증", description = "Portone에서 결제 후 검증을 합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "생성 성공"),
                    @ApiResponse(responseCode = "400", description = "생성 실패")
            })
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

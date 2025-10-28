package org.example.eatopia.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.review.dto.request.ReviewRequest;
import org.example.eatopia.domain.review.dto.response.ReviewResponse;
import org.example.eatopia.domain.review.service.command.ReviewCommandService;
import org.example.eatopia.domain.review.service.query.ReviewQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/v1/orders/{orderDetailId}/review")
    public ResponseEntity<Response<ReviewResponse>> createReview(@PathVariable Long orderDetailId,
                                                                 @Valid @RequestBody ReviewRequest request,
                                                                 @AuthenticationPrincipal UserPrincipal authUser) {

        ReviewResponse response = reviewCommandService.createReview(orderDetailId, authUser.getId(), request);

        return ResponseEntity.ok(Response.success(response));
    }
}

package org.example.eatopia.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.review.dto.request.ReviewRequest;
import org.example.eatopia.domain.review.dto.request.ReviewSearchCondition;
import org.example.eatopia.domain.review.dto.response.ReviewResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSearchResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSellerResponse;
import org.example.eatopia.domain.review.service.command.ReviewCommandService;
import org.example.eatopia.domain.review.service.query.ReviewQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/v1/products/{productId}/reviews")
    public ResponseEntity<Response<Page<ReviewSearchResponse>>> searchReviews(@PathVariable Long productId,
                                                                              @ModelAttribute ReviewSearchCondition condition,
                                                                              @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewSearchResponse> response = reviewQueryService.searchReviews(productId, condition, pageable);

        return ResponseEntity.ok(Response.success(response));
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/v1/seller/reviews")
    public ResponseEntity<Response<Page<ReviewSellerResponse>>> getReviewsBySeller(@RequestParam(required = false) Long productId,
                                                                                   @ModelAttribute ReviewSearchCondition condition,
                                                                                   @AuthenticationPrincipal UserPrincipal authUser,
                                                                                   @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {


        Page<ReviewSellerResponse> response = reviewQueryService.getReviewsBySeller(productId, authUser.getId(), condition, pageable);

        return ResponseEntity.ok(Response.success(null));
    }
}

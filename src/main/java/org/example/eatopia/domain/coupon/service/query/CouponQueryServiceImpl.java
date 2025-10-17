package org.example.eatopia.domain.coupon.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.exception.CouponErrorCode;
import org.example.eatopia.domain.coupon.exception.CouponException;
import org.example.eatopia.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponQueryServiceImpl implements CouponQueryService {

    private final CouponRepository couponRepository;

    public CouponResponse getCoupon(Long couponId) {

        Coupon coupon = couponRepository.findCouponById(couponId)
                .orElseThrow(() -> new CouponException(CouponErrorCode.NotFoundCouponId));

        CouponResponse response = CouponResponse.from(coupon);

        return response;
    }
}
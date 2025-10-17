package org.example.eatopia.domain.coupon.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.repository.CouponRepository;
import org.example.eatopia.domain.coupon.validator.CouponValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponCommandServiceImpl implements CouponCommandService {

    private final CouponRepository couponRepository;
    private final CouponValidator couponValidator;

    public CouponResponse createCoupon(CouponCreateRequest request) {

        couponValidator.couponCreateValidate(request);
        
        // 유저의 role 확인 후 구매자일 때 예외처리

        Coupon coupon = Coupon.of(request);

        couponRepository.save(coupon);

        CouponResponse response = CouponResponse.of(coupon);

        return response;
    }
}

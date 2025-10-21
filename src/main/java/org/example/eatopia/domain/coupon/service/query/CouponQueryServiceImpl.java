package org.example.eatopia.domain.coupon.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.exception.CouponErrorCode;
import org.example.eatopia.domain.coupon.exception.CouponException;
import org.example.eatopia.domain.coupon.repository.CouponRepository;
import org.example.eatopia.domain.user.dto.CouponCreatorInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 쿠폰 조회(단건/목록) 기능을 제공하는 쿼리 서비스 구현.
 * <p>
 * - 읽기 전용 트랜잭션에서 쿠폰과 생성자 정보를 조합해 {@link CouponResponse} 로 반환합니다.<br>
 * - 유효하지 않은 쿠폰 ID 요청 시 {@link CouponException} 을 발생시켜 표준 에러 흐름을 유지합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponQueryServiceImpl implements CouponQueryService {

    private final CouponRepository couponRepository;

    // 쿠폰 단건 조회
    public CouponResponse getCoupon(Long couponId) {

        Coupon coupon = couponRepository.findByIdWithUser(couponId)
                .orElseThrow(() -> new CouponException(CouponErrorCode.INVALID_COUPON));

        CouponCreatorInfoResponse creator = CouponCreatorInfoResponse.of(
                coupon.getUser().getId(),
                coupon.getUser().getName(),
                coupon.getUser().getCompany(),
                coupon.getUser().getUserRole()
        );

        return CouponResponse.of(coupon, creator);
    }

    // 생성된 모든 쿠폰 목록 페이징 조회
    public Page<CouponResponse> getCreatedCoupons(Pageable pageable) {

        Page<Coupon> coupons = couponRepository.findAll(pageable);

        Page<CouponResponse> response = coupons.map(coupon -> {
            CouponCreatorInfoResponse creator = CouponCreatorInfoResponse.of(
                    coupon.getUser().getId(),
                    coupon.getUser().getName(),
                    coupon.getUser().getCompany(),
                    coupon.getUser().getUserRole()
            );

            return CouponResponse.of(coupon, creator);
        });

        return response;
    }
}
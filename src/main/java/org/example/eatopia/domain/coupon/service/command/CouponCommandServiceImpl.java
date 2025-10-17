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

    // 상수
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

    private final CouponRepository couponRepository;
    private final CouponValidator couponValidator;

    public CouponResponse createCoupon(CouponCreateRequest request) {

        couponValidator.couponCreateValidate(request);

        String code = generateUniqueCode();

        // TODO: 유저의 role 확인 후 구매자일 때 예외처리

        Coupon coupon = Coupon.of(request, code);

        couponRepository.save(coupon);

        CouponResponse response = CouponResponse.from(coupon);

        return response;
    }


    // 헬퍼메서드
    private String generateUniqueCode() {

        String code;

        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);

            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();

        } while (// 중복 시 재시도
                couponRepository.existsByCode(code)
        );

        return code;
    }
}

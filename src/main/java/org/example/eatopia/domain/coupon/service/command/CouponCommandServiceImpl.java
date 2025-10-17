package org.example.eatopia.domain.coupon.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.repository.CouponRepository;
import org.example.eatopia.domain.coupon.validator.CouponValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponCommandServiceImpl implements CouponCommandService {

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

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String code;

        do {
            StringBuilder sb = new StringBuilder(8);

            for (int i = 0; i < 8; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = sb.toString();
        } while (couponRepository.existsByCode(code)); // 중복 시 재시도

        return code;
    }
}

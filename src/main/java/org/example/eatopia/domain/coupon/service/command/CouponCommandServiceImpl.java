package org.example.eatopia.domain.coupon.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.entity.CouponIssue;
import org.example.eatopia.domain.coupon.exception.CouponErrorCode;
import org.example.eatopia.domain.coupon.exception.CouponException;
import org.example.eatopia.domain.coupon.repository.CouponIssueRepository;
import org.example.eatopia.domain.coupon.repository.CouponRepository;
import org.example.eatopia.domain.coupon.validator.CouponValidator;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
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


    private final UserQueryService userQueryService;
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final CouponValidator couponValidator;

    public CouponResponse createCoupon(CouponCreateRequest request) {

        couponValidator.couponCreateValidate(request);

        String code = generateUniqueCode();

        // TODO: 유저의 role 확인 후 구매자일 때 예외처리

        Coupon coupon = Coupon.of(request, code);

        couponRepository.save(coupon);

        return CouponResponse.from(coupon);
    }

    public void downloadCoupon(UserPrincipal authUser, Long couponId) {

        // 1. 쿠폰 다운로드한 사용자 조회
        User user = userQueryService.getUserEntityById(authUser.getId());

        // 2. 다운로드 대상 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(CouponErrorCode.INVALID_COUPON));

        // 3. 중복발급 검증
        boolean alreadyIssued = couponIssueRepository.existsByUserIdAndCouponId(authUser.getId(), couponId);
        if (alreadyIssued) {
            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON_ISSUE);
        }

        // 4. 사전 검증
        couponValidator.validateDownloadable(coupon);

        // 5. 쿠폰 발급
        coupon.issue();

        // 6. 쿠폰 발급 내역 생성
        CouponIssue newIssue = CouponIssue.of(user, coupon);
        couponIssueRepository.save(newIssue);
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

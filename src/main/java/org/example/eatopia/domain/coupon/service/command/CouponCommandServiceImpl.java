package org.example.eatopia.domain.coupon.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.consts.Const;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.entity.CouponIssue;
import org.example.eatopia.domain.coupon.exception.CouponErrorCode;
import org.example.eatopia.domain.coupon.exception.CouponException;
import org.example.eatopia.domain.coupon.repository.CouponIssueRepository;
import org.example.eatopia.domain.coupon.repository.CouponRepository;
import org.example.eatopia.domain.coupon.validator.CouponValidator;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.dto.CouponCreatorInfoResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.eatopia.common.core.consts.Const.RANDOM;

/**
 * 쿠폰 생성/다운로드(발급) 명령을 처리하는 서비스 구현.
 * <p>
 * - 생성 시 비즈니스 검증 후 고유 코드 생성 및 저장.<br>
 * - 다운로드(발급) 시 잠금 기반 단건 조회로 동시성 제어, 중복 발급 방지, 사전 검증 수행.<br>
 * - 트랜잭션 경계 내에서 쿠폰 상태 변경 및 발급 이력 저장을 원자적으로 보장합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CouponCommandServiceImpl implements CouponCommandService {

    private final UserQueryService userQueryService;
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final CouponValidator couponValidator;

    // 쿠폰 생성 처리
    public CouponResponse createCoupon(CouponCreateRequest request, UserPrincipal userAuth) {

        // 쿠폰 생성에 관한 검증
        couponValidator.couponCreateValidate(request);

        // 쿠폰 코드 생성
        String code = generateUniqueCode();
        User user = userQueryService.getUserEntityById(userAuth.getId());
        Coupon coupon = Coupon.of(request, user, code);

        couponRepository.save(coupon);

        // 쿠폰 생성자에 관한 응답 dto 생성
        CouponCreatorInfoResponse creator = CouponCreatorInfoResponse.of(user.getId(), user.getName(), user.getCompany(), user.getUserRole());

        return CouponResponse.of(coupon, creator);
    }

    // 쿠폰 다운로드(발급) 처리
    public void downloadCoupon(UserPrincipal authUser, Long couponId) {

        // 1. 쿠폰 다운로드한 사용자, 다운로드 대상 쿠폰 조회
        User user = userQueryService.getUserEntityById(authUser.getId());
        Coupon coupon = couponRepository.findWithLockById(couponId)
                .orElseThrow(() -> new CouponException(CouponErrorCode.INVALID_COUPON));

        // 2. 중복발급 검증
        boolean alreadyIssued = couponIssueRepository.existsByUserIdAndCouponId(authUser.getId(), couponId);
        if (alreadyIssued) {
            throw new CouponException(CouponErrorCode.DUPLICATE_COUPON_ISSUE);
        }

        // 3. 사전 검증
        couponValidator.validateDownloadable(coupon);

        // 4. 쿠폰 발급
        coupon.issue();

        // 5. 쿠폰 발급 내역 생성
        CouponIssue newIssue = CouponIssue.of(user, coupon);

        couponIssueRepository.save(newIssue);
    }

    public void deleteCoupon(UserPrincipal userAuth, Long couponId) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(CouponErrorCode.INVALID_COUPON));

        if (!userAuth.hasRole(UserRole.ADMIN)) {
            Long loginUserId = userAuth.getId();
            Long couponCreatorId = coupon.getUser().getId();

            couponValidator.isOwned(loginUserId, couponCreatorId);
        }

        coupon.softDelete();
    }

    // 헬퍼메서드
    // 고유한 쿠폰 코드 생성(중복 체크하며 재시도)
    private String generateUniqueCode() {

        String couponCode;

        do {
            StringBuilder sb = new StringBuilder(Const.CODE_LENGTH);

            for (int i = 0; i < Const.CODE_LENGTH; i++) {
                sb.append(Const.CODE_CHARS.charAt(RANDOM.nextInt(Const.CODE_CHARS.length())));
            }
            couponCode = sb.toString();

        } while (// 중복 시 재시도
                couponRepository.existsByCode(couponCode)
        );

        return couponCode;
    }
}

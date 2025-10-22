package org.example.eatopia.domain.coupon.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.exception.CouponErrorCode;
import org.example.eatopia.domain.coupon.exception.CouponException;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 쿠폰 생성/다운로드/사용 가능 여부를 검증하는 유효성 검사기.
 * <p>
 * - 생성 시 기간·할인값·수량 범위를 점검합니다.<br>
 * - 다운로드 시 기간 만료/수량 소진 여부를 점검합니다.<br>
 * - 사용 시 삭제/시작 전/만료 여부 등을 점검합니다.<br>
 * 본 컴포넌트는 상태를 가지지 않는 순수 검증 로직만 포함합니다.
 */
@Component
@RequiredArgsConstructor
public class CouponValidator {

    // 쿠폰 생성 요청값 유효성 검증
    public void couponCreateValidate(CouponCreateRequest request) {

        LocalDateTime startDate = request.startAt();
        LocalDateTime endDate = request.endAt();

        // 종료일이 시작일보다 앞서는지
        if (endDate.isBefore(startDate)) {
            throw new CouponException(CouponErrorCode.ILLEGAL_END_DATE);
        }

        // 시작일이 현재 이전인지
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new CouponException(CouponErrorCode.PAST_START_DATE);
        }

        // 퍼센트 범위 검증
        if (Boolean.TRUE.equals(request.isPercent())) {
            BigDecimal value = request.discountValue();
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new CouponException(CouponErrorCode.INVALID_PERCENT_RANGE);
            }
        }

        // 금액형 할인 시 최소주문금액 < 할인금액 체크
        if (Boolean.FALSE.equals(request.isPercent())
                && request.minOrderAmount() != null
                && request.discountValue() != null
                && request.minOrderAmount().compareTo(request.discountValue()) < 0) {
            throw new CouponException(CouponErrorCode.INVALID_MIN_ORDER_AMOUNT);
        }

        // 수량 검증
        if (request.totalQuantity() != null && request.totalQuantity() < 0) {
            throw new CouponException(CouponErrorCode.INVALID_TOTAL_QUANTITY);
        }
    }

    // 쿠폰 다운로드 가능 여부 검증
    public void validateDownloadable(Coupon coupon) {

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(coupon.getEndAt())) {
            throw new CouponException(CouponErrorCode.INVALID_DOWNLOAD_DATE);
        }

        if (coupon.getTotalQuantity() != null && coupon.getRemainingQuantity() <= 0) {
            throw new CouponException(CouponErrorCode.SOLD_OUT_COUPON);
        }
    }

    // 쿠폰 사용 가능 여부 검증
    public void validateUsable(Coupon coupon, User user) {

        LocalDateTime now = LocalDateTime.now();

        if (coupon.getDeletedAt() != null) {
            throw new CouponException(CouponErrorCode.DELETED_COUPON);
        }

        if (now.isBefore(coupon.getStartAt())) {
            throw new CouponException(CouponErrorCode.NOT_STARTED_COUPON);
        }

        if (now.isAfter(coupon.getEndAt())) {
            throw new CouponException(CouponErrorCode.EXPIRED_COUPON);
        }

        //// 신규 유저 전용인데, 신규 유저가 아닌 경우
        //if (Boolean.TRUE.equals(coupon.getIsNewUserOnly()) && !user.getIsNewUser) {
        //    throw new CouponException(CouponErrorCode.ONLY_FOR_NEW_USER);
        //}
        //// 첫 주문 전용인데, 첫 주문이 아닌 경우
        //if (Boolean.TRUE.equals(coupon.getIsFirstOrderOnly()) && !isFirstOrder) {
        //    throw new CouponException(CouponErrorCode.ONLY_FOR_FIRST_ORDER);
        //}
    }

    // 현재 로그인한 사용자와 쿠폰 생성자가 일치하는지 검증
    public void isOwned(UserPrincipal user, Coupon coupon) {
        
        Long loginUserId = user.getId();
        Long couponCreatorId = coupon.getUser().getId();

        if (!loginUserId.equals(couponCreatorId)) {
            throw new CouponException(CouponErrorCode.NOT_SAME_USER);
        }
    }
}
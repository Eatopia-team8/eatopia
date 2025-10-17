package org.example.eatopia.common.infra.security;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.CommonErrorCode;
import org.example.eatopia.domain.auth.dto.AuthUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security Context에서 현재 로그인된 AuthUser 정보를 추출하는 유틸리티 클래스.
 * <p>
 * Controller나 Service 계층에서 @AuthenticationPrincipal 없이도 사용자 정보를 가져올 때 사용
 */
public class SecurityUtil {

    private SecurityUtil() {
        // 인스턴스화 방지
    }

    /**
     * SecurityContext에서 현재 로그인된 AuthUser 객체를 반환
     * <p>
     *
     * @return 현재 로그인된 AuthUser 객체
     * @throws GlobalException 인증 정보가 Security Context에 없을 경우 (401 Unauthorized)
     */
    public static AuthUser getCurrentUser() {
        // 1. SecurityContext에서 Authentication 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 인증 정보가 없거나 익명 사용자일 경우 예외 처리
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            // 인증 정보가 없으므로 401 Unauthorized 에러에 해당하는 예외를 던집니다.
            throw new GlobalException(CommonErrorCode.UNAUTHORIZED);
        }

        // 3. Principal(인증주체) 객체가 AuthUser 타입인지 확인하고 반환
        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthUser) {
            return (AuthUser) principal;
        } else {
            // JWT 필터가 작동하지 않았거나 Principal 타입이 잘못된 경우
            throw new GlobalException(CommonErrorCode.UNAUTHORIZED); // 401로 처리
        }
    }
}

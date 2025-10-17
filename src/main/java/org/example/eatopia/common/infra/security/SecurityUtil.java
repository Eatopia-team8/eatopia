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
     * JWT 필터(JwtAuthenticationFilter)에서 AuthUser를 Principal로 저장했을 때 사용 가능
     *
     * @return 현재 로그인된 AuthUser 객체
     * @throws GlobalException 인증 정보가 Security Context에 없을 경우 발생
     */
    public static AuthUser getAuthUser() {
        // 1. SecurityContext에서 Authentication객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 인증 정보가 없거나 익명 사용자일 경우 예외 처리
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new GlobalException(CommonErrorCode.UNAUTHORIZED);
        }

        //3. Principal(인증주체) 객체가 AuthUser타입인지 확인하고 반환
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthUser) {
            return (AuthUser) principal;
        } else {
            throw new RuntimeException("현재 Principal 객체가 예상된 AuthUser 타입이 아닙니다.");
        }
    }

}

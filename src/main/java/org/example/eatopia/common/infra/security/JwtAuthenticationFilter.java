package org.example.eatopia.common.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // HTTP 헤더에서 토큰을 추출할 때 사용되는 접두사
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Request Header에서 토큰 추출
        String jwt = resolveToken(request);

        // 2. 토큰이 존재하고 유효성 검사를 통과하면 (jwtProvider 내부에서 로그 처리됨)
        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            // 3. 토큰이 유효하면 인증 객체를 생성
            Authentication authentication = jwtProvider.getAuthentication(jwt);

            // 4. 인증 객체가 null이 아닌 경우 (인증 성공)
            if (authentication != null) {
                // Security Context에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 다음 필터로 요청 전달 (인증 성공 여부와 관계없이)
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 토큰 정보를 꺼내오는 메소드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // StringUtils.hasText()를 사용하여 null, 빈 문자열, 공백만 있는 문자열 모두 체크
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // "Bearer " 접두사 제거
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
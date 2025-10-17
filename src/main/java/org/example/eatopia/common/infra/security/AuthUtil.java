package org.example.eatopia.common.infra.security;

import org.example.eatopia.domain.user.enttiy.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

/**
 * JWT 토큰 생성에 필요한 Spring Security Authentication 객체를 생성하는 유틸리티.
 * <p>
 * User 엔티티를 받아 Authentication 객체를 생성하는 중복 코드를 처리합니다.
 */
public class AuthUtil {
    private AuthUtil() {
        // 인스턴스화 방지
    }

    /**
     * User 엔티티를 기반으로 JWT 생성에 필요한 Authentication 객체를 생성합니다.
     * <p>
     * Principal로 User ID(String)를 사용합니다. 이 객체는 **JWT 생성에 필요한 클레임 데이터**를 담는 용도로 사용되며,
     * 실제 시스템 인증은 JwtAuthenticationToken이 담당합니다.
     *
     * @param user User 엔티티
     * @return Authentication 객체 (인증 전 상태)
     */
    public static Authentication createAuthentication(User user) {
        // 1. 권한 문자열 생성 (예: ROLE_BUYER)
        String authority = "ROLE_" + user.getUserRole().name();

        // 2. Spring Security의 기본 UserDetails 객체 생성
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(authority))
        );

        // 3. UsernamePasswordAuthenticationToken 반환 (JWT 생성에 필요한 데이터를 담는 임시 객체 역할)
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}
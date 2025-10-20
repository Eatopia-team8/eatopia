package org.example.eatopia.common.infra.security;

import org.example.eatopia.common.core.dto.JwtPayload;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    public AuthService(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * 사용자 객체(User)를 기반으로 JWT 생성에 필요한 페이로드와 토큰을 생성
     *
     * @param user 인증된 User 객체
     * @return 생성된 JWT
     */
    public String issueToken(User user) {
        // 1. 권한 생성 (user.getRole() -> user.getUserRole()로 수정)
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name())
        );

        // 2. JWT Payload DTO 생성
        JwtPayload jwtPayload = JwtPayload.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .authorities(authorities)
                .build();

        // 3. JWT 토큰 생성
        return jwtProvider.createToken(jwtPayload);
    }
}
package org.example.eatopia.domain.auth.dto;

import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.enttiy.User;

import java.time.LocalDateTime;

/**
 * 로그인 성공 후 응답 정보를 담는 DTO (record 사용)
 *
 * @param id        사용자 고유 ID
 * @param email     사용자 이메일
 * @param name      사용자 이름
 * @param createdAt 계정 생성 일시
 * @param role      사용자 역할
 * @param token     발급된 JWT 토큰
 */
public record AuthLoginResponse(
        Long id,
        String email,
        String name,
        LocalDateTime createdAt,
        UserRole role,
        String token
) {
    /**
     * User 엔티티와 발급된 JWT 토큰을 응답 DTO로 변환
     *
     * @param user  User 엔티티
     * @param token 발급된 JWT 토큰
     * @return AuthLoginResponse DTO
     */
    public static AuthLoginResponse of(User user, String token) {
        return new AuthLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt(),
                user.getUserRole(),
                token
        );
    }
}

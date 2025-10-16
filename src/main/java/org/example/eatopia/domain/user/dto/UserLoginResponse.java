package org.example.eatopia.domain.user.dto;

import org.example.eatopia.domain.user.enttiy.User;

import java.time.LocalDateTime;

/**
 * 로그인 성공 후 응답 정보를 담는 DTO (record 사용)
 *
 * @param id        사용자 고유 ID
 * @param email     사용자 이메일
 * @param name      사용자 이름
 * @param createdAt 계정 생성 일시
 * @param token     발급된 JWT 토큰
 */
public record UserLoginResponse(
        Long id,
        String email,
        String name,
        LocalDateTime createdAt,
        String token

) {
    /**
     * User 엔티티와 발급된 JWT 토큰을 응답 DTO로 변환합니다.
     *
     * @param user  User 엔티티
     * @param token 발급된 JWT 토큰
     * @return UserLoginResponse DTO
     */
    public static UserLoginResponse of(User user, String token) {
        return new UserLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt(),
                token
        );
    }
}

package org.example.eatopia.domain.user.dto;

import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.enttiy.User;

import java.time.LocalDateTime;

/**
 * 회원가입 성공 후 응답 정보를 담는 DTO (record 사용)
 * <p>
 * 생성된 사용자 정보와 발급된 JWT 토큰을 포함합니다.
 *
 * @param id        사용자 고유 ID
 * @param email     사용자 이메일
 * @param name      사용자 이름
 * @param createdAt 계정 생성 일시
 * @param role      사용자 권한
 * @param token     발급된 JWT 토큰 (회원가입 후 바로 발급)
 */
public record UserSignUpResponse(
        Long id,
        String email,
        String name,
        LocalDateTime createdAt,
        UserRole role,
        String token
) {
    /**
     * User 엔티티와 발급된 JWT 토큰을 응답 DTO(UserSignUpResponse)로 변환합니다.
     *
     * @param user  User 엔티티
     * @param token 발급된 JWT 토큰
     * @return UserSignUpResponse DTO
     */
    // from 정적 팩토리 메소드 사용
    public static UserSignUpResponse from(User user, String token) {
        return new UserSignUpResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt(),
                user.getUserRole(),
                token
        );
    }
}
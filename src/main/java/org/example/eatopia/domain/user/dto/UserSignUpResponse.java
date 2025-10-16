package org.example.eatopia.domain.user.dto;

import java.time.LocalDateTime;
import org.example.eatopia.domain.user.enttiy.User;

/**
 * 회원가입 성공 후 응답 정보를 담는 DTO
 *
 * @param id        사용자 고유 ID
 * @param email     사용자 이메일
 * @param name      사용자 이름
 * @param createdAt 계정 생성 일시
 */
public record UserSignUpResponse(
    Long id,
    String email,
    String name,
    LocalDateTime createdAt
) {

    /**
     * User 엔티티를 응답 DTO(UserSignUpResponse)로 변환합니다.
     *
     * @param user User 엔티티
     * @return UserSignUpResponse DTO
     */
    public static UserSignUpResponse from(User user) {
        return new UserSignUpResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getCreatedAt()
        );
    }
}
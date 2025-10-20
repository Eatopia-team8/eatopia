package org.example.eatopia.domain.user.dto;

import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.entity.User;

import java.time.LocalDateTime;

/**
 * 사용자 상세 조회 응답 DTO
 */
public record UserDetailResponse(
        Long id,
        String email,
        String name,
        String address,
        String company,
        UserRole role,
        LocalDateTime createdAt

) {
    /**
     * User 엔티티를 상세 조회응답 DTO로 변환
     */
    public static UserDetailResponse of(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getAddress(),
                user.getCompany(),
                user.getUserRole(),
                user.getCreatedAt()
        );
    }
}


package org.example.eatopia.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.SoftDeleteEntity;

/**
 * Refresh Token 정보를 저장하는 엔티티.
 * (인증/보안 책임)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "auth_tokens")
public class RefreshToken extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID (PK)를 저장하여 User 엔티티와 연결
    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String refreshToken;

    @Builder(access = AccessLevel.PRIVATE) // 빌더 접근 제어자 지정
    private RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    /**
     * 리프레시 토큰 정보를 생성하는 정적 팩토리 메소드.
     */
    public static RefreshToken create(Long userId, String refreshToken) {
        return RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 리프레시 토큰 값을 업데이트하는 비즈니스 메서드.
     */
    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

}
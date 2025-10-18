package org.example.eatopia.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 토큰 정보를 저장하는 엔티티.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId; // 토큰 소유자 ID

    @Column(nullable = false, unique = true, length = 100)
    private String token; // 실제 재설정 토큰 값

    @Column(nullable = false)
    private LocalDateTime expiryDate; // 토큰 만료 시간

    @Builder
    private PasswordResetToken(Long userId, String token, LocalDateTime expiryDate) {
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    /**
     * 토큰이 만료되었는지 확인하는 비즈니스 메서드
     */
    public boolean isExpired() {
        return this.expiryDate.isBefore(LocalDateTime.now());
    }

    /**
     * 토큰의 유효성을 확인하고, 사용된 경우 만료 시간을 현재 시점으로 설정합니다.
     */
    public void markAsUsed() {
        // 이미 만료되었거나 사용된 토큰은 재사용 불가하도록 즉시 만료 처리
        this.expiryDate = LocalDateTime.now();
    }
}

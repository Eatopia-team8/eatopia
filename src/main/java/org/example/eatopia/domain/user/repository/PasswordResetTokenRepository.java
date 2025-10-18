package org.example.eatopia.domain.user.repository;

import org.example.eatopia.domain.user.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * 토큰 값으로 유효한 토큰을 조회
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * 사용자 ID로 유효한 토큰을 조회
     */
    Optional<PasswordResetToken> findByUserId(Long userId);
}

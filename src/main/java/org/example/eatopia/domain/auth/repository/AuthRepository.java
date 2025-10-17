package org.example.eatopia.domain.auth.repository;

import org.example.eatopia.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * AuthToken 엔티티를 관리하는 리포지토리
 */
public interface AuthRepository extends JpaRepository<RefreshToken, Long> {
    // 사용자 ID를 기준으로 Refresh Token 조회
    Optional<RefreshToken> findByUserId(Long userId);
}

package org.example.eatopia.domain.auth.repository;

import jakarta.persistence.LockModeType;
import org.example.eatopia.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

/**
 * AuthToken 엔티티를 관리하는 리포지토리
 */
public interface AuthRepository extends JpaRepository<RefreshToken, Long> {

    // 사용자 ID를 기준으로 Refresh Token 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByUserId(Long userId);
}

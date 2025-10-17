package org.example.eatopia.domain.auth.dto;

import lombok.Getter;
import org.example.eatopia.domain.user.config.UserRole;

/**
 * JWT 인증 성공 후 SecurityContext에 저장되는 사용자 주체(Principal) 객체.
 * <p>
 * Spring Security에 종속되지 않는 순수한 DTO 형태로 사용
 */
@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final String name;
    private final UserRole userRole;

    public AuthUser(Long id, String email, String name, UserRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userRole = role;
    }
}
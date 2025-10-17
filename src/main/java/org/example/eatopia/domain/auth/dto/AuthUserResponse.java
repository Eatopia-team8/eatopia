package org.example.eatopia.domain.auth.dto;

/**
 * JWT에서 추출한 현재 로그인 사용자의 핵심 정보를 담는 DTO (record 사용)
 *
 * @param id   사용자 고유 ID
 * @param name 사용자 이름
 * @param role 사용자 역할
 */
public record AuthUserResponse(
        Long id,
        String name,
        String email,
        String role
) {
    /**
     * AuthUser 객체를 응답 DTO로 변환
     *
     * @param authUser Security Context에서 추출된 AuthUser 객체
     * @return AuthUserResponse DTO
     */
    public static AuthUserResponse from(AuthUser authUser) {
        String roleName = authUser.getUserRole().name();
        return new AuthUserResponse(
                authUser.getId(),
                authUser.getName(),
                authUser.getEmail(),
                roleName
        );
    }
}

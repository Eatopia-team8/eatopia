package org.example.eatopia.domain.auth.dto;

import lombok.Getter;
import org.example.eatopia.domain.user.config.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * JWT 인증 성공 후 SecurityContext에 저장되는 사용자 주체(Principal) 객체.
 * <p>
 * 이 객체는 @AuthenticationPrincipal로 Controller에 직접 주입됩니다.
 */
@Getter
public class AuthUser implements UserDetails {

    private final Long id;
    private final String email; // JWT 'sub' 클레임에 ID 대신 이메일을 사용할 경우를 대비해 email도 유지
    private final String name;
    private final Collection<? extends GrantedAuthority> authorities;
    private final UserRole userRole;
    private final String password; // UserDetails 필수 구현 필드 (실제 패스워드는 사용하지 않음)


    public AuthUser(Long id, String email, String name, UserRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userRole = role;
        this.password = ""; // 토큰 인증에서는 사용되지 않으므로 빈 값

        // GrantedAuthority 리스트 생성
        this.authorities = List.of(new SimpleGrantedAuthority(role.getKey()));
    }

    // --- UserDetails 필수 메서드 구현 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * UserDetails에서 Principal의 식별자를 반환
     * JWT의 'sub' 클레임에 해당하는 ID(문자열)를 반환
     */
    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

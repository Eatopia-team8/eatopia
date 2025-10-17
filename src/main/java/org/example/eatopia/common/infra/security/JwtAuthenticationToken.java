package org.example.eatopia.common.infra.security;

import org.example.eatopia.domain.auth.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * JWT 인증을 위해 사용하는 커스텀 Authentication Token.
 * <p>
 * Username/Password 없이도 인증된 상태(Authenticated)를 표현
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthUser principal;
    private final String token;

    public JwtAuthenticationToken(AuthUser principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;

        super.setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    // JWT는 생성 즉시 인증이 완료되므로, 이 메서드는 사용하지 않습니다.
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (!isAuthenticated) {
            super.setAuthenticated(false);
        } else {
            super.setAuthenticated(true);
        }
    }
}
package org.example.eatopia.common.core.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Builder
public class JwtPayload {
    private final Long userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
}

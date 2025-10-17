package org.example.eatopia.common.core.dto;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Builder
public record JwtPayload(
        Long userId,
        String email,
        String name,
        Collection<? extends GrantedAuthority> authorities
) {

}

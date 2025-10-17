package org.example.eatopia.common.infra.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.common.core.dto.JwtPayload;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT(JSON Web Token) 생성 및 유효성 검증을 담당하는 컴포넌트
 */
@Component
@Slf4j
public class JwtProvider {

    private final long tokenValidityInMilliseconds;
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.access-token-expiration-milliseconds}")
    private long accessTokenExpirationMs;
    private Key key;

    public JwtProvider(@Value("${jwt.secret-key}") String secretKey,
                       @Value("${jwt.access-token-expiration-milliseconds}") long tokenValidity) {
        // 생성자에서는 필드 초기화만 담당합니다.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidity;
    }

    /**
     * JWT 시크릿 키를 Base64 디코딩하여 Key 객체로 초기화 (PostConstruct를 사용하여 중복 제거)
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        // tokenValidityInMilliseconds는 생성자에서 이미 초기화되었으므로 그대로 둡니다.
    }

    /**
     * JWT 토큰 생성.
     */
    public String createToken(JwtPayload payload) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        String authorities = payload.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(payload.userId().toString())    // 사용자 ID를 Subject로 사용
                .claim("auth", authorities)                 // 권한 정보
                .claim("email", payload.email())            // 이메일 정보
                .claim("name", payload.name())              // 이름 정보
                .signWith(key, SignatureAlgorithm.HS512)    // 서명
                .setExpiration(validity)                    // 만료 시간
                .compact();
    }

    /**
     * JWT 토큰에서 인증 정보를 추출하고 UserPrincipal 객체를 Principal로 설정
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims == null) {
            return null;
        }

        Object authClaim = claims.get("auth");
        if (authClaim == null) {
            log.warn("JWT에 권한 정보가 없습니다.");
            return null;
        }
        try {
            // 1. Claims에서 필요한 정보 추출 및 변환
            Long userId = Long.parseLong(claims.getSubject());
            String userEmail = (String) claims.get("email");
            String userName = (String) claims.get("name"); // 토큰에 값이 있어야 추출 가능

            if (userName == null) {
                log.warn("JWT 토큰에서 'name' 클레임을 찾을 수 없거나 값이 null입니다. 클레임 목록: {}", claims.keySet());
            }

            // 2. 권한 정보 변환 (Spring Security 내부용)
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(authClaim.toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            // 3. UserRole Enum 추출
            String authorityStr = authorities.iterator().next().getAuthority();
            // ROLE_ 접두사 제거
            String roleName = authorityStr.substring(authorityStr.indexOf('_') + 1);
            UserRole userRole = UserRole.valueOf(roleName);

            // 4. 커스텀 UserPrincipal 객체를 생성 (인증 주체: Principal)
            UserPrincipal principal = new UserPrincipal(userId, userEmail, userName, userRole);

            // 5. 인증 토큰 반환
            return new JwtAuthenticationToken(principal, token, authorities); // JwtAuthenticationToken 반환

        } catch (IllegalArgumentException e) {
            log.error("JWT 클레임 처리 중 형식 변환 오류 발생: {}", e.getMessage());
            return null;
        } catch (RuntimeException e) {
            log.error("JWT 클레임 처리 중 예상치 못한 런타임 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            return null;
        }
    }
}

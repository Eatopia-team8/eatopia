package org.example.eatopia.common.infra.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.common.core.dto.JwtPayload;
import org.example.eatopia.domain.auth.dto.AuthUser;
import org.example.eatopia.domain.user.config.UserRole;
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
 * <p>
 * 설정 파일에서 시크릿 키와 만료 시간을 읽어와 JWT 관련 작업을 처리
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
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidity;
    }

    /**
     * JWT 시크릿 키를 Base64 디코딩하여 Key 객체로 초기화
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(JwtPayload payload) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        String authorities = payload.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(payload.getUserId().toString()) // 사용자 ID를 Subject로 사용
                .claim("auth", authorities)                 // 권한 정보
                .claim("email", payload.getEmail())         // 추가 정보 (필요시)
                .signWith(key, SignatureAlgorithm.HS512)    // 서명
                .setExpiration(validity)                    // 만료 시간
                .compact();
    }

    /**
     * JWT 토큰에서 인증 정보를 추출하고 AuthUser 객체를 Principal로 설정
     *
     * @param token JWT 문자열
     * @return 인증 객체 (Authentication)
     */
    public Authentication getAuthentication(String token) {
        // 토큰에서 클레임(Claims) 추출
        Claims claims = parseClaims(token);

        if (claims == null) {
            return null;
        }

        // JWT claims에서 권한(auth) 정보를 추출
        Object authClaim = claims.get("auth");
        if (authClaim == null) {
            log.warn("JWT에 권한 정보가 없습니다.");
            return null; // 권한 없으면 인증 실패 처리
        }
        try {
            // 1. Claims에서 필요한 정보 추출 및 변환
            Long userId = Long.parseLong(claims.getSubject());
            String userEmail = (String) claims.get("email");
            String userName = (String) claims.get("name");

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

            // 4. 커스텀 AuthUser 객체를 생성 (인증 주체: Principal)
            AuthUser principal = new AuthUser(userId, userEmail, userName, userRole);

            // 5. 인증 토큰 반환
            return new JwtAuthenticationToken(principal, token, authorities); // JwtAuthenticationToken 반환

        } catch (IllegalArgumentException e) {
            // JWT 클레임의 형식 변환 중 오류 발생 시 처리
            log.error("JWT 클레임 처리 중 형식 변환 오류 발생: {}", e.getMessage());
            return null;
        } catch (RuntimeException e) {
            // 예상치 못한 기타 런타임 오류
            log.error("JWT 클레임 처리 중 예상치 못한 런타임 오류 발생: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 토큰 유효성 검사 (로그 출력)
     *
     * @param token JWT 문자열
     * @return 유효성 여부
     */
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

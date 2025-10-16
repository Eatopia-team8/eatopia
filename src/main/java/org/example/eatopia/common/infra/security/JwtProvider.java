package org.example.eatopia.common.infra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-milliseconds}")
    private long accessTokenExpirationMs;

    private Key key;

    /**
     * JWT 시크릿 키를 Base64 디코딩하여 Key 객체로 초기화
     */
    @PostConstruct
    public void init() {
        // 시크릿 키를 Base64 디코딩
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Key 객체 생성
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Authentication 객체로부터 JWT 액세스 토큰을 생성
     *
     * @param authentication 인증 객체
     * @return JWT 문자열
     */
    public String generateToken(Authentication authentication) {
        // 권한 정보를 문자열로 변환 (예: ROLE_BUYER,ROLE_SELLER)
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        // 토큰 만료 시간 설정
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationMs);

        // JWT 빌더를 사용하여 토큰 생성
        return Jwts.builder()
                .setSubject(authentication.getName()) // 토큰 제목 (일반적으로 사용자 ID)
                .claim("auth", authorities)          // 사용자 권한 정보
                .setExpiration(accessTokenExpiresIn) // 만료 시간
                .signWith(key)                       // 서명
                .compact();                          // 토큰 생성
    }

    /**
     * JWT 토큰에서 인증 정보를 추출
     *
     * @param token JWT 문자열
     * @return 인증 객체 (Authentication)
     */
    public Authentication getAuthentication(String token) {
        // 토큰에서 클레임(Claims) 추출
        Claims claims = parseClaims(token);

        // JWT claims에서 권한(auth) 정보를 추출
        Object authClaim = claims.get("auth");
        if (authClaim == null) {
            throw new RuntimeException("JWT에 권한 정보가 없습니다.");
        }

        // 권한 문자열을 SimpleGrantedAuthority 객체 컬렉션으로 변환
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authClaim.toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        // Spring Security의 User 객체를 생성 (인증 주체: Principal)
        User principal = new User(claims.getSubject(), "", authorities);

        // 인증 토큰 반환
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰의 유효성을 검사
     *
     * @param token JWT 문자열
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        // Jwts 파서를 사용하여 토큰의 유효성 검사 및 서명 확인
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 유효하지 않은 토큰, 만료된 토큰 등 예외 발생 시 false 반환
            return false;
        }
    }

    // JWT 토큰에서 Claims(본문)를 파싱하여 추출
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
package org.example.eatopia.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.infra.security.JwtProvider;
import org.example.eatopia.domain.auth.dto.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.AuthLoginResponse;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 사용자의 로그인 정보를 검증하고, 성공 시 JWT 토큰을 발급
     *
     * @param request 로그인 요청 DTO
     * @return UserLoginResponse DTO
     */
    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        //1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                //사용자가 존재하지 않으면 예외 발생
                .orElseThrow(() -> new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS));

        // 2. 비밀번호 검증
        // request.password()와 DB에 저장된 암호화된 비밀번호를 비교
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            // 비밀번호가 일치하지 않으면 예외 발생
            throw new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS);
        }

        // 3. JWT 토큰 생성에 필요한 ID, Name, Email 추출
        final Long userId = user.getId();
        final String userName = user.getName();
        final String userEmail = user.getEmail();

        // JWT 생성을 위한 Spring Security 인증 객체(Authentication) 생성
        String authority = "ROLE_" + user.getUserRole().name();

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                userId.toString(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(authority))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        // 토큰 생성
        String jwt = jwtProvider.generateToken(authentication, userId, userName, userEmail);
        return AuthLoginResponse.of(user, jwt);
    }
}

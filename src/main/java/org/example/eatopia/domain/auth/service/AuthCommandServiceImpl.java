package org.example.eatopia.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.common.core.dto.JwtPayload;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.infra.security.JwtProvider;
import org.example.eatopia.domain.auth.dto.login.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.login.AuthLoginResponse;
import org.example.eatopia.domain.auth.dto.signup.AuthSignUpRequest;
import org.example.eatopia.domain.auth.dto.signup.AuthSignUpResponse;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthCommandServiceImpl implements AuthCommandService {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /**
     * 회원가입 및 토큰 발행에 필요한 JwtPayload를 생성합니다.
     */
    private JwtPayload createPayload(User user) {
        // 사용자 권한 정보를 GrantedAuthority 컬렉션으로 변환
        Collection<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name())
        );

        log.info("User's name being sent to JwtPayload: {}", user.getName());

        return JwtPayload.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .authorities(authorities)
                .build();
    }

    @Override
    public AuthSignUpResponse signUp(AuthSignUpRequest request) {
        // 1. 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new GlobalException(UserErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화 및 User 엔티티 생성
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = AuthSignUpRequest.toEntity(request, encodedPassword, request.role());

        // 3. 사용자 정보 저장
        User savedUser = userRepository.save(user);

        // 4. JWT 토큰 생성 로직 (AuthCommandServiceImpl이 직접 수행)
        JwtPayload payload = createPayload(savedUser);
        String jwt = jwtProvider.createToken(payload);

        // 5. 응답 DTO로 변환 후 반환
        return AuthSignUpResponse.of(savedUser, jwt);
    }

    /**
     * 사용자의 로그인 정보를 검증하고, 성공 시 JWT 토큰을 발급
     */
    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        //1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS);
        }

        // 3. 토큰 발급 로직 (AuthCommandServiceImpl이 직접 수행)
        JwtPayload payload = createPayload(user);
        String jwt = jwtProvider.createToken(payload);

        return AuthLoginResponse.of(user, jwt);
    }
}
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
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.example.eatopia.domain.user.validator.UserValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthCommandServiceImpl implements AuthCommandService {

    // 회원가입 시 허용할 역할 목록
    private static final List<UserRole> ALLOWED_SIGNUP_ROLES = Arrays.asList(
            UserRole.BUYER,
            UserRole.SELLER,
            UserRole.ADMIN
    );

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    //JWT 토큰 발행 로직을 분리하여 중복을 제거
    private String issueToken(User user) {
        JwtPayload payload = createJwtPayloadFromUser(user);
        return jwtProvider.createToken(payload);
    }


    //회원가입 및 토큰 발행에 필요한 JwtPayload를 User 엔티티로부터 생성
    private JwtPayload createJwtPayloadFromUser(User user) {
        // 사용자 권한 정보를 GrantedAuthority 컬렉션으로 변환
        Collection<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name())
        );

        log.info("User name for JWT: {}", user.getName());

        return JwtPayload.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .authorities(authorities)
                .build();
    }

    @Override
    public AuthSignUpResponse signUp(AuthSignUpRequest request) {

        // 1. 역할(Role) 유효성 검증
        if (!ALLOWED_SIGNUP_ROLES.contains(request.role())) {
            throw new GlobalException(AuthErrorCode.ACCESS_DENIED, "허용되지 않은 사용자 역할입니다.");
        }

        // 2. 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new GlobalException(UserErrorCode.DUPLICATE_EMAIL);
        }

        // 3. 비밀번호 인코딩 및 User 엔티티 생성
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.signUp(
                request.email(),
                encodedPassword,
                request.name(),
                request.role()
        );

        // 4. 사용자 정보 저장
        User savedUser = userRepository.save(user);

        // 5. JWT 토큰 생성 (issueToken 헬퍼 메서드 사용)
        String jwt = issueToken(savedUser);

        // 6. 응답 DTO로 변환 후 반환
        return AuthSignUpResponse.of(savedUser, jwt);
    }

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        //1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS));

        // 2. 비밀번호 검증
        userValidator.validateCurrentPassword(request.password(), user.getPassword());

        // 3. 토큰 발급 로직 (issueToken 헬퍼 메서드 사용)
        String jwt = issueToken(user);

        return AuthLoginResponse.of(user, jwt);
    }
}

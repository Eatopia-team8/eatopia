package org.example.eatopia.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.infra.security.AuthService;
import org.example.eatopia.common.infra.security.JwtProvider;
import org.example.eatopia.domain.auth.dto.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.AuthLoginResponse;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthService authService;

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
                // 사용자가 존재하지 않으면 예외 발생
                .orElseThrow(() -> new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS));

        // 2. 비밀번호 검증 (비즈니스 로직 유지)
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            // 비밀번호가 일치하지 않으면 예외 발생
            throw new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS);
        }

        // 3. ⭐️ 인증 객체 생성 및 토큰 발급 로직을 AuthService에 위임
        // 도메인 서비스는 유저 객체를 넘겨주고, 보안 관련 처리를 위임한다.
        String jwt = authService.issueToken(user);

        return AuthLoginResponse.of(user, jwt);
    }
}

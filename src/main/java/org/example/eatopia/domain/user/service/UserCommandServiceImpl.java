package org.example.eatopia.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.infra.security.JwtProvider;
import org.example.eatopia.domain.user.dto.UserLoginRequest;
import org.example.eatopia.domain.user.dto.UserLoginResponse;
import org.example.eatopia.domain.user.dto.UserSignUpRequest;
import org.example.eatopia.domain.user.dto.UserSignUpResponse;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
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
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public UserSignUpResponse signUp(UserSignUpRequest request) {
        // 1. 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            // 이미 이메일이 존재하면, 도메인 에러코드를 사용하여 예외 발생
            throw new GlobalException(UserErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화 및 User 엔티티 생성
        String encodedPassword = passwordEncoder.encode(request.password());
        // DTO의 정적 팩토리 메소드를 사용하여 엔티티 생성
        User user = UserSignUpRequest.toEntity(request, encodedPassword);

        // 3. 사용자 정보 저장
        User savedUser = userRepository.save(user);

        // 4. JWT 토큰 생성 로직 (회원가입 후 바로 발급)

        // 4-1. JWT 생성을 위한 Spring Security 인증 객체(Authentication) 생성
        String authority = "ROLE_" + savedUser.getUserRole().name();
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(),
                savedUser.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(authority))
        );
        // Authentication 객체는 토큰에 담길 사용자 정보와 권한을 포함.
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        // 4-2. 토큰 생성
        String jwt = jwtProvider.generateToken(authentication);

        // 5. 저장된 엔티티와 토큰을 포함하여 응답 DTO로 변환 후 반환
        return UserSignUpResponse.from(savedUser, jwt);
    }

    /**
     * 사용자의 로그인 정보를 검증하고, 성공 시 JWT 토큰을 발급합니다.
     *
     * @param request 로그인 요청 DTO
     * @return UserLoginResponse DTO
     */
    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        //1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                //사용자가 존재하지 않으면 예외 발생
                .orElseThrow(() -> new GlobalException(UserErrorCode.UNAUTHORIZED_CREDENTIALS));

        // 2. 비밀번호 검증
        // request.password()와 DB에 저장된 암호화된 비밀번호를 비교
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            // 비밀번호가 일치하지 않으면 예외 발생
            throw new GlobalException(UserErrorCode.UNAUTHORIZED_CREDENTIALS);
        }
        // JWT 생성을 위한 Spring Security 인증 객체(Authentication) 생성
        String authority = "ROLE_" + user.getUserRole().name();
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(authority))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        // 토큰 생성
        String jwt = jwtProvider.generateToken(authentication);
        return UserLoginResponse.from(user, jwt);
    }


}

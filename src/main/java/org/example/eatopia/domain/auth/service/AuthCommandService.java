package org.example.eatopia.domain.auth.service;

import org.example.eatopia.domain.auth.dto.login.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.login.AuthLoginResponse;
import org.example.eatopia.domain.auth.dto.signup.AuthSignUpRequest;
import org.example.eatopia.domain.auth.dto.signup.AuthSignUpResponse;

public interface AuthCommandService {
    // 로그인 메소드
    AuthLoginResponse login(AuthLoginRequest request);

    // 회원가입을 처리하고 JWT 토큰을 포함한 응답을 반환
    AuthSignUpResponse signUp(AuthSignUpRequest request);
}
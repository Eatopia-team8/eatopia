package org.example.eatopia.domain.auth.service;

import org.example.eatopia.domain.auth.dto.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.AuthLoginResponse;

public interface AuthCommandService {
    // 로그인 메소드
    AuthLoginResponse login(AuthLoginRequest request);
}

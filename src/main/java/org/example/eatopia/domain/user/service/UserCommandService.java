package org.example.eatopia.domain.user.service;

import org.example.eatopia.domain.user.dto.UserSignUpRequest;
import org.example.eatopia.domain.user.dto.UserSignUpResponse;

public interface UserCommandService {
    // 회원가입을 처리하고 JWT 토큰을 포함한 응답을 반환
    UserSignUpResponse signUp(UserSignUpRequest request);
}

package org.example.eatopia.domain.user.service;

import org.example.eatopia.domain.user.dto.UserSignUpRequest;
import org.example.eatopia.domain.user.dto.UserSignUpResponse;

public interface UserCommandService {

    //회원가입 메소드
    UserSignUpResponse signUp(UserSignUpRequest request);
}

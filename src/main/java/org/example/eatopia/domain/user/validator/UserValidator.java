package org.example.eatopia.domain.user.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final PasswordEncoder passwordEncoder;

    //사용자가 입력한 비밀번호가 DB에 저장된 비밀번호와 일치하는지 검증
    public void validateCurrentPassword(String rawOldPassword, String encodedSavedPassword) {
        if (!passwordEncoder.matches(rawOldPassword, encodedSavedPassword)) {
            //비밀번호가 일치하지않으면 에러발생
            throw new GlobalException(AuthErrorCode.UNAUTHORIZED_CREDENTIALS);
        }
    }

}

package org.example.eatopia.domain.user.service.command;

import org.example.eatopia.domain.user.dto.UserPasswordChangeRequest;
import org.example.eatopia.domain.user.dto.UserPasswordResetRequest;

public interface UserCommandService {
    /**
     * 로그인 상태에서 본인의 비밀번호를 변경
     */
    void changePassword(Long userId, UserPasswordChangeRequest request);

    /**
     * [로그인 불필요] 이메일과 재설정 토큰을 사용하여 비밀번호를 재설정
     */
    void resetPassword(UserPasswordResetRequest request);
}
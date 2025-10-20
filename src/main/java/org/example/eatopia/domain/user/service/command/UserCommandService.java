package org.example.eatopia.domain.user.service.command;

import org.example.eatopia.domain.user.dto.UserEmailForPasswordReset;
import org.example.eatopia.domain.user.dto.UserPasswordChangeRequest;
import org.example.eatopia.domain.user.dto.UserPasswordResetRequest;
import org.example.eatopia.domain.user.dto.UserUpdateProfileRequest;

public interface UserCommandService {

    //로그인 상태에서 본인의 비밀번호를 변경
    void changePassword(Long userId, UserPasswordChangeRequest request);


    // 이메일로 비밀번호 재설정 토큰을 요청하고 생성
    String requestPasswordResetToken(UserEmailForPasswordReset request);


    //이메일과 재설정 토큰을 사용하여 비밀번호를 재설정
    void resetPassword(UserPasswordResetRequest request);

    //유저 정보(회사이름, 주소) 업데이트
    void updateProfile(Long id, UserUpdateProfileRequest request);
}
package org.example.eatopia.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 사용자 로그인 요청 정보를 담는 DTO (record 사용)
 *
 * @param email    사용자 이메일 (계정 ID)
 * @param password 사용자 비밀번호
 */
public record UserLoginRequest(

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Size(max = 50)
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        String password

) {
}

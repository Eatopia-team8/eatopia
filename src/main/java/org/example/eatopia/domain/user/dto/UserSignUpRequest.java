package org.example.eatopia.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.eatopia.domain.user.enttiy.User;

/**
 * 사용자 회원가입 요청 정보를 담는 DTO
 *
 * @param email    사용자 이메일 (계정 ID로 사용)
 * @param password 사용자 비밀번호
 * @param name     사용자 이름
 */
public record UserSignUpRequest(
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Size(max = 50)
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(max = 30)
        String name

) {
    /**
     * 요청 DTO(UserSignUpRequest)를 User 엔티티로 변환합니다.
     *
     * @param request         회원가입 요청 DTO
     * @param encodedPassword 암호화된 비밀번호
     * @return User 엔티티
     */
    public static User toEntity(UserSignUpRequest request, String encodedPassword) {
        return User.signUp(
                request.email(),
                encodedPassword,
                request.name()
        );
    }
}
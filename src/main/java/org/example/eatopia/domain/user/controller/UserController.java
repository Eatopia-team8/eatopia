package org.example.eatopia.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.auth.dto.AuthUser;
import org.example.eatopia.domain.auth.dto.AuthUserResponse;
import org.example.eatopia.domain.user.dto.UserSignUpRequest;
import org.example.eatopia.domain.user.dto.UserSignUpResponse;
import org.example.eatopia.domain.user.service.UserCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserCommandService userCommandService;

    /**
     * 사용자 회원가입을 처리하고 JWT 토큰을 즉시 발급
     *
     * @param request 회원가입에 필요한 정보(이메일, 비밀번호, 이름)를 담은 DTO
     * @return 생성된 사용자의 정보와 JWT 토큰이 담긴 응답 DTO
     */
    @PostMapping("/signup")
    public ResponseEntity<Response<UserSignUpResponse>> signUp(@Valid @RequestBody UserSignUpRequest request) {
        // 중간 변수를 활용하여 서비스 호출 및 결과 받기
        UserSignUpResponse response = userCommandService.signUp(request);
        // 공통 응답 포맷(ApiResponse.success)에 맞춰 성공 응답 반환
        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * JWT 토큰을 기반으로 현재 로그인된 사용자의 ID와 이름을 반환
     *
     * @param authUser Security Context에 저장된 AuthUser 객체
     * @return AuthUserResponse DTO (ID, Name, Email, Role 포함)
     */
    @GetMapping("userInfo")
    public ResponseEntity<Response<AuthUserResponse>> getCurrentUserInfo(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        AuthUserResponse response = AuthUserResponse.from(authUser);
        return ResponseEntity.ok(Response.success(response));
    }
}
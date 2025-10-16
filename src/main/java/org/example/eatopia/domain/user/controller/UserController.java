package org.example.eatopia.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.user.dto.UserLoginRequest;
import org.example.eatopia.domain.user.dto.UserLoginResponse;
import org.example.eatopia.domain.user.dto.UserSignUpRequest;
import org.example.eatopia.domain.user.dto.UserSignUpResponse;
import org.example.eatopia.domain.user.service.UserCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 인증 및 계정 관련 요청을 처리하는 컨트롤러.
 * <p>
 * 주로 회원가입, 로그인 등의 기능을 담당합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class UserController {
    // User 도메인의 CUD 작업을 담당하는 서비스 주입
    private final UserCommandService userCommandService;

    /**
     * 사용자 회원가입을 처리하고 JWT 토큰을 즉시 발급합니다.
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
     * 사용자 로그인을 처리하고 JWT 토큰을 발급합니다.
     *
     * @param request 로그인 정보(이메일, 비밀번호)
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<Response<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        //중간변수를 활용하여 서비스 호출 및 결과받기
        UserLoginResponse response = userCommandService.login(request);
        //공통응답포맷에 맞춰 성공응답반환
        return ResponseEntity.ok(Response.success(response));
    }
}
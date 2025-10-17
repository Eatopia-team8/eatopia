package org.example.eatopia.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.auth.dto.login.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.login.AuthLoginResponse;
import org.example.eatopia.domain.auth.dto.signup.AuthSignUpRequest;
import org.example.eatopia.domain.auth.dto.signup.AuthSignUpResponse;
import org.example.eatopia.domain.auth.service.AuthCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthCommandService authCommandService;

    /**
     * 사용자 회원가입을 처리하고 JWT 토큰을 즉시 발급
     *
     * @param request 회원가입에 필요한 정보(이메일, 비밀번호, 이름)를 담은 DTO
     * @return 생성된 사용자의 정보와 JWT 토큰이 담긴 응답 DTO
     */
    @PostMapping("/signup")
    public ResponseEntity<Response<AuthSignUpResponse>> signUp(@Valid @RequestBody AuthSignUpRequest request) {
        // 중간 변수를 활용하여 서비스 호출 및 결과 받기
        AuthSignUpResponse response = authCommandService.signUp(request);
        // 공통 응답 포맷(ApiResponse.success)에 맞춰 성공 응답 반환
        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * 사용자 로그인을 처리하고 JWT 토큰을 발급
     *
     * @param request 로그인 정보(이메일, 비밀번호)
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<Response<AuthLoginResponse>> login(@Valid @RequestBody AuthLoginRequest request) {

        //중간변수를 활용하여 서비스 호출 및 결과받기
        AuthLoginResponse response = authCommandService.login(request);

        //공통응답포맷에 맞춰 성공응답반환
        return ResponseEntity.ok(Response.success(response));
    }

}

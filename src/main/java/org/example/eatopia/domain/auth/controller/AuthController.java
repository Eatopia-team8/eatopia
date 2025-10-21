package org.example.eatopia.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.auth.dto.request.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.request.AuthSignUpRequest;
import org.example.eatopia.domain.auth.dto.response.AuthLoginResponse;
import org.example.eatopia.domain.auth.dto.response.AuthSignUpResponse;
import org.example.eatopia.domain.auth.service.AuthCommandService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthCommandService authCommandService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Response<AuthSignUpResponse>> signUp(@Valid @RequestBody AuthSignUpRequest request) {
        // 중간 변수를 활용하여 서비스 호출 및 결과 받기
        AuthSignUpResponse response = authCommandService.signUp(request);
        // 공통 응답 포맷(ApiResponse.success)에 맞춰 성공 응답 반환
        return ResponseEntity.ok(Response.success(response));
    }

    //로그인처리
    @PostMapping("/login")
    public ResponseEntity<Response<AuthLoginResponse>> login(@Valid @RequestBody AuthLoginRequest request) {

        //중간변수를 활용하여 서비스 호출 및 결과받기
        AuthLoginResponse response = authCommandService.login(request);

        //공통응답포맷에 맞춰 성공응답반환
        return ResponseEntity.ok(Response.success(response));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout(@AuthenticationPrincipal UserPrincipal authUser) {

        authCommandService.logout(authUser.getId());

        return ResponseEntity.ok(Response.success(null));
    }

    //회원탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<Response<Void>> withdrawUser(@AuthenticationPrincipal UserPrincipal authUser) {

        //토큰에서 추출한 userId를 사용
        authCommandService.withdrawUser(authUser.getId());

        return ResponseEntity.ok(Response.success(null));
    }
}

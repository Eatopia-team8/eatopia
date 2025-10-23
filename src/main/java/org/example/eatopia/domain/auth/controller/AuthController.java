package org.example.eatopia.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "로그인, 로그아웃, 회원탈퇴기능", description = "로그인, 로그아웃, 회원탈퇴기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthCommandService authCommandService;

    //회원가입
    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    @PostMapping("/signup")
    public ResponseEntity<Response<AuthSignUpResponse>> signUp(@Valid @RequestBody AuthSignUpRequest request) {
        // 중간 변수를 활용하여 서비스 호출 및 결과 받기
        AuthSignUpResponse response = authCommandService.signUp(request);
        // 공통 응답 포맷(ApiResponse.success)에 맞춰 성공 응답 반환
        return ResponseEntity.ok(Response.success(response));
    }

    //로그인처리
    @Operation(summary = "회원 로그인", description = "아이디와 비밀번호를 사용하여 로그인하고 토큰을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공, 세션 생성"),
                    @ApiResponse(responseCode = "401", description = "로그인 실패 (잘못된 아이디 또는 비밀번호)")
            })
    @PostMapping("/login")
    public ResponseEntity<Response<AuthLoginResponse>> login(@Valid @RequestBody AuthLoginRequest request) {

        //중간변수를 활용하여 서비스 호출 및 결과받기
        AuthLoginResponse response = authCommandService.login(request);

        //공통응답포맷에 맞춰 성공응답반환
        return ResponseEntity.ok(Response.success(response));
    }

    //로그아웃
    @Operation(summary = "회원 로그아웃", description = "토큰을 사용하여 로그아웃합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "401", description = "로그아웃 실패")
            })
    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout(@AuthenticationPrincipal UserPrincipal authUser) {

        authCommandService.logout(authUser.getId());

        return ResponseEntity.ok(Response.success(null));
    }

    //회원탈퇴
    @Operation(summary = "회원 삭제", description = "특정 회원을 삭제합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
            })
    @DeleteMapping("/withdraw")
    public ResponseEntity<Response<Void>> withdrawUser(@AuthenticationPrincipal UserPrincipal authUser) {

        //토큰에서 추출한 userId를 사용
        authCommandService.withdrawUser(authUser.getId());

        return ResponseEntity.ok(Response.success(null));
    }
}

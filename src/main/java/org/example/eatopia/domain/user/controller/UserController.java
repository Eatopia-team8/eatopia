package org.example.eatopia.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.user.dto.*;
import org.example.eatopia.domain.user.service.command.UserCommandService;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    /**
     * JWT 토큰을 기반으로 현재 로그인된 사용자의 ID와 이름을 반환
     *
     * @param authUser Security Context에 저장된 AuthUser 객체
     * @return AuthUserResponse DTO (ID, Name, Email, Role 포함)
     */
    @GetMapping("/userInfo")
    public ResponseEntity<Response<UserResponse>> getCurrentUserInfo(@AuthenticationPrincipal UserPrincipal authUser) {
        UserResponse response = UserResponse.from(authUser);
        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * 전체 사용자 목록을 조회 (페이지네이션 적용)
     */
    @GetMapping("/allUsers")
    public ResponseEntity<Response<Page<UserDetailResponse>>> getAllUsers(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        // Query Service를 사용하여 페이지 단위로 목록 조회
        Page<UserDetailResponse> usersPage = userQueryService.getAllUsers(pageable);
        return ResponseEntity.ok(Response.success(usersPage)); // Page 객체 반환
    }

    /**
     * 특정 ID를 가진 사용자 상세 정보를 조회
     */
    @GetMapping("/user-detail/{userId}")
    public ResponseEntity<Response<UserDetailResponse>> getUserById(@PathVariable Long userId) {

        UserDetailResponse response = userQueryService.getUserById(userId);
        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * 비밀번호 변경 (Token값 체크하여 ID 기반으로 변경)
     */
    @PatchMapping("/change-password")
    public ResponseEntity<Response<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal authUser,
            @Valid @RequestBody UserPasswordChangeRequest request) {
        //UserCommandService를 통해 비밀번호 변경 처리
        userCommandService.changePassword(authUser.getId(), request);

        //성공시 200 OK 반환
        return ResponseEntity.ok(Response.success(null));
    }

    /**
     * 비밀번호 재설정 토큰발급 요청
     */
    @PostMapping("/request-password-reset")
    public ResponseEntity<Response<String>> requestPasswordResetToken(@Valid @RequestBody UserEmailForPasswordReset request) {

        String token = userCommandService.requestPasswordResetToken(request);
        return ResponseEntity.ok(Response.success(token));
    }

    /**
     * [로그인 불필요] 이메일과 재설정 토큰을 사용하여 비밀번호를 재설정
     */
    @PostMapping("/password-reset")
    public ResponseEntity<Response<Void>> resetPassword(@Valid @RequestBody UserPasswordResetRequest request) {
        userCommandService.resetPassword(request);
        return ResponseEntity.ok(Response.success(null));
    }
}
package org.example.eatopia.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.user.dto.UserDetailResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.dto.UserResponse;
import org.example.eatopia.domain.user.service.command.UserCommandService;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("allUsers")
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
}
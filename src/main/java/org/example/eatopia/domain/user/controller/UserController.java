package org.example.eatopia.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.dto.request.UserMailRequest;
import org.example.eatopia.domain.user.dto.request.UserPasswordChangeRequest;
import org.example.eatopia.domain.user.dto.request.UserPasswordResetRequest;
import org.example.eatopia.domain.user.dto.request.UserUpdateProfileRequest;
import org.example.eatopia.domain.user.dto.response.UserDetailResponse;
import org.example.eatopia.domain.user.dto.response.UserResponse;
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

    //JWT토큰을 기반으로 현재 로그인된 사용자의 ID와 이름을 반환
    @GetMapping("/userInfo")
    public ResponseEntity<Response<UserResponse>> getCurrentUserInfo(@AuthenticationPrincipal UserPrincipal authUser) {

        UserResponse response = UserResponse.from(authUser);
        return ResponseEntity.ok(Response.success(response));
    }

    //관리자만 사용가능한 유저목록보기
    @GetMapping("/admin-use-userList")
    public ResponseEntity<Response<Page<UserDetailResponse>>> getAllUsersForAdmin(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        Page<UserDetailResponse> usersPage = userQueryService.getAllUsersForAdmin(authUser.getId(), pageable);
        return ResponseEntity.ok(Response.success(usersPage));
    }

    //특정 ID를 가진 사용자 상세정보 조회
    @GetMapping("/user-detail/{userId}")
    public ResponseEntity<Response<UserDetailResponse>> getUserById(@PathVariable Long userId) {

        UserDetailResponse response = userQueryService.getUserById(userId);
        return ResponseEntity.ok(Response.success(response));
    }

    //이메일로 비밀번호 재설정토큰 발급
    @PostMapping("/newpassword-foremail")
    public ResponseEntity<Response<String>> newPasswordForEmail(@Valid @RequestBody UserMailRequest request) {

        String token = userCommandService.newPasswordForEmail(request);

        return ResponseEntity.ok(Response.success(token));
    }

    //비밀번호 변경 (Token값 체크하여 ID 기반으로 변경)
    @PatchMapping("/change-password")
    public ResponseEntity<Response<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal authUser,
            @Valid @RequestBody UserPasswordChangeRequest request) {

        //UserCommandService를 통해 비밀번호 변경 처리
        userCommandService.changePassword(authUser.getId(), request);

        //성공시 200 OK 반환
        return ResponseEntity.ok(Response.success(null));
    }

    //이메일과 재설정 토큰을 사용하여 비밀번호를 재설정
    @PostMapping("/password-reset")
    public ResponseEntity<Response<Void>> resetPassword(@Valid @RequestBody UserPasswordResetRequest request) {
        userCommandService.resetPassword(request);
        return ResponseEntity.ok(Response.success(null));
    }

    //사용자의 프로필정보(주소, 회사명)을 업데이트
    @PatchMapping("/update-profile")
    public ResponseEntity<Response<Void>> updateProfile(
            @AuthenticationPrincipal UserPrincipal authUser,
            @Valid @RequestBody UserUpdateProfileRequest request) {

        userCommandService.updateProfile(authUser.getId(), request);

        return ResponseEntity.ok(Response.success(null));
    }

}
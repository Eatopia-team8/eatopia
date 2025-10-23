package org.example.eatopia.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "유저정보보기, 비밀번호 변경", description = "유저정보보기, 비밀번호 변경")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    //JWT토큰을 기반으로 현재 로그인된 사용자의 ID와 이름을 반환
    @Operation(summary = "현재 로그인된 사용자의 ID와 이름을 반환", description = "JWT토큰을 기반으로 현재 로그인된 사용자의 ID와 이름을 반환",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    @GetMapping("/userInfo")
    public ResponseEntity<Response<UserResponse>> getCurrentUserInfo(@AuthenticationPrincipal UserPrincipal authUser) {

        UserResponse response = UserResponse.from(authUser);
        return ResponseEntity.ok(Response.success(response));
    }

    //관리자만 사용가능한 유저목록보기
    @Operation(summary = "유저목록보기 기능", description = "관리자일때 사용가능한 유저목록보기 기능",
            responses = {
                    @ApiResponse(responseCode = "201", description = "목록 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    @GetMapping("/admin-use-userList")
    public ResponseEntity<Response<Page<UserDetailResponse>>> getAllUsersForAdmin(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        Page<UserDetailResponse> usersPage = userQueryService.getAllUsersForAdmin(authUser.getId(), pageable);
        return ResponseEntity.ok(Response.success(usersPage));
    }

    //관리자일때 사용할 수 있는 유저검색(이메일, 이름으로 검색가능)
    @Operation(summary = "유저목록보기 기능", description = "관리자일때 사용가능한 이메일이나 이름으로검색하는 기능",
            responses = {
                    @ApiResponse(responseCode = "201", description = "검색성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    @GetMapping("/search")
    public ResponseEntity<Response<Page<UserDetailResponse>>> searchUsers(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("keyword") String keyword,
            @PageableDefault(size = 50) Pageable pageable) {

        Page<UserDetailResponse> usersPage = userQueryService.searchUsers(principal.getId(), keyword, pageable);
        return ResponseEntity.ok(Response.success(usersPage));
    }

    //특정 ID를 가진 사용자 상세정보 조회
    @Operation(summary = "상세정보 조회", description = "Id로 찾은 특정유저 상세정보",
            responses = {
                    @ApiResponse(responseCode = "201", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    @GetMapping("/user-detail/{userId}")
    public ResponseEntity<Response<UserDetailResponse>> getUserById(@PathVariable Long userId) {

        UserDetailResponse response = userQueryService.getUserById(userId);
        return ResponseEntity.ok(Response.success(response));
    }

    //이메일로 비번초기화 토큰보내기
    @Operation(summary = "이메일로 비번초기화 토큰보내기", description = "이메일로 비번초기화 토큰보내기",
            responses = {
                    @ApiResponse(responseCode = "201", description = "생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    @PostMapping("/newpassword-foremail")
    public ResponseEntity<Response<Void>> newPasswordForEmail(@Valid @RequestBody UserMailRequest request) {

        userCommandService.newPasswordForEmail(request);

        return ResponseEntity.ok(Response.success());
    }

    //비밀번호 변경 (Token값 체크하여 ID 기반으로 변경)
    @Operation(summary = "비밀번호 변경", description = "토큰값 이용해서 비밀번호 변경",
            responses = {
                    @ApiResponse(responseCode = "201", description = "변경 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
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
    @Operation(summary = "이메일과 토큰사용해서 비밀번호 재설정", description = "이메일과 재설정 토큰을 사용하여 비밀번호를 재설정",
            responses = {
                    @ApiResponse(responseCode = "201", description = "변경 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    @PostMapping("/password-reset")
    public ResponseEntity<Response<Void>> resetPassword(@Valid @RequestBody UserPasswordResetRequest request) {
        userCommandService.resetPassword(request);
        return ResponseEntity.ok(Response.success(null));
    }

    //사용자의 프로필정보(주소, 회사명)을 업데이트
    @Operation(summary = "사용자 프로필정보 수정", description = "주소, 회사명(판매자거나 관리자일때만) 변경",
            responses = {
                    @ApiResponse(responseCode = "201", description = "생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            })
    @PatchMapping("/update-profile")
    public ResponseEntity<Response<Void>> updateProfile(
            @AuthenticationPrincipal UserPrincipal authUser,
            @Valid @RequestBody UserUpdateProfileRequest request) {

        userCommandService.updateProfile(authUser.getId(), request);

        return ResponseEntity.ok(Response.success(null));
    }

}
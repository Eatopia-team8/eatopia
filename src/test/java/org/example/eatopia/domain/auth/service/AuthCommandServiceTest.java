package org.example.eatopia.domain.auth.service;

import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.auth.controller.AuthController;
import org.example.eatopia.domain.auth.dto.request.AuthLoginRequest;
import org.example.eatopia.domain.auth.dto.request.AuthSignUpRequest;
import org.example.eatopia.domain.auth.dto.response.AuthLoginResponse;
import org.example.eatopia.domain.auth.dto.response.AuthSignUpResponse;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @MockitoBean
    private AuthCommandService authCommandService;

    private UserPrincipal testUserPrincipal;

    private AuthSignUpResponse signUpResponse;
    private AuthLoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        signUpResponse = new AuthSignUpResponse(
                1L,
                "test@user.com",
                "테스터",
                LocalDateTime.now(),
                UserRole.BUYER,
                "fake.access.token.signup"
        );

        loginResponse = new AuthLoginResponse(
                1L,
                "test@user.com",
                "테스터",
                LocalDateTime.now(),
                UserRole.BUYER,
                "fake.access.token.login"
        );

        testUserPrincipal = new UserPrincipal(
                1L,
                "test@user.com",
                "테스터",
                UserRole.BUYER
        );
    }

    @Test
    @DisplayName("signUp - 회원가입 성공 및 응답 확인")
    void signUp_Success() {

        AuthSignUpRequest request = new AuthSignUpRequest("new@user.com", "password123", "새유저", UserRole.BUYER);
        when(authCommandService.signUp(any(AuthSignUpRequest.class))).thenReturn(signUpResponse);

        ResponseEntity<Response<AuthSignUpResponse>> responseEntity = authController.signUp(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Response<AuthSignUpResponse> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);

        assertEquals(signUpResponse.token(), responseBody.getData().token());

        verify(authCommandService, times(1)).signUp(request);
    }

    @Test
    @DisplayName("login - 로그인 성공 및 응답 확인")
    void login_Success() {

        AuthLoginRequest request = new AuthLoginRequest("test@user.com", "password123");
        when(authCommandService.login(any(AuthLoginRequest.class))).thenReturn(loginResponse);

        ResponseEntity<Response<AuthLoginResponse>> responseEntity = authController.login(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Response<AuthLoginResponse> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);

        assertEquals(loginResponse.token(), responseBody.getData().token());

        verify(authCommandService, times(1)).login(request);
    }

    @Test
    @DisplayName("logout - 로그아웃 성공 및 서비스 위임 확인")
    void logout_Success() {

        Long userId = testUserPrincipal.getId();
        doNothing().when(authCommandService).logout(anyLong());

        ResponseEntity<Response<Void>> responseEntity = authController.logout(testUserPrincipal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Response<Void> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        verify(authCommandService, times(1)).logout(userId);
    }

    @Test
    @DisplayName("withdrawUser - 회원 탈퇴 성공 및 서비스 위임 확인")
    void withdrawUser_Success() {

        Long userId = testUserPrincipal.getId();
        doNothing().when(authCommandService).withdrawUser(anyLong());

        ResponseEntity<Response<Void>> responseEntity = authController.withdrawUser(testUserPrincipal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Response<Void> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);

        verify(authCommandService, times(1)).withdrawUser(userId);
    }
}
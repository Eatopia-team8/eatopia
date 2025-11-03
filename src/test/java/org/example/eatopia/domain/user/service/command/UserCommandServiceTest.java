package org.example.eatopia.domain.user.service.command;

import org.example.eatopia.common.infra.mail.MailService;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.dto.request.UserMailRequest;
import org.example.eatopia.domain.user.dto.request.UserPasswordChangeRequest;
import org.example.eatopia.domain.user.dto.request.UserPasswordResetRequest;
import org.example.eatopia.domain.user.dto.request.UserUpdateProfileRequest;
import org.example.eatopia.domain.user.entity.PasswordResetToken;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.repository.PasswordResetTokenRepository;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class UserCommandServiceTest {

    private final String TEST_EMAIL = "test@eatopia.com";
    private final String ADMIN_EMAIL = "admin@eatopia.com";
    private final String OLD_PASSWORD = "oldPassword123!";
    private final String VALID_TOKEN = "valid-test-token";
    private final String TEST_NAME = "테스트 사용자";
    private User testUser; // 일반 테스트용
    private User adminUser; // 관리자 테스트용
    private User userForTokenTest; // 토큰 발송 테스트용
    private User userForTokenFailureTest; // 토큰 실패 테스트용

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private MailService mailService;
    
    @BeforeEach
    void setUp() {

        // 1. 일반 테스트 사용자 생성
        testUser = User.signUp(
                TEST_EMAIL,
                passwordEncoder.encode(OLD_PASSWORD),
                TEST_NAME,
                UserRole.BUYER
        );
        testUser = userRepository.save(testUser);

        // 2. 관리자 사용자 생성
        adminUser = User.signUp(
                ADMIN_EMAIL,
                passwordEncoder.encode(OLD_PASSWORD),
                "관리자",
                UserRole.ADMIN
        );
        adminUser = userRepository.save(adminUser);

        // 3. 유효한 비밀번호 재설정 토큰 생성 및 저장
        PasswordResetToken token = PasswordResetToken.builder()
                .userId(testUser.getId())
                .token(VALID_TOKEN)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        tokenRepository.save(token);

        // 4. 토큰 발송 테스트를 위한 독립적인 유저 생성
        userForTokenTest = User.signUp(
                "token@test.com",
                passwordEncoder.encode(OLD_PASSWORD),
                "토큰 발송 유저",
                UserRole.BUYER
        );
        userForTokenTest = userRepository.save(userForTokenTest);

        // 5. 만료된 토큰 실패 테스트를 위한 독립적인 유저 생성
        userForTokenFailureTest = User.signUp(
                "failure@test.com",
                passwordEncoder.encode(OLD_PASSWORD),
                "토큰 실패 유저",
                UserRole.BUYER
        );
        userForTokenFailureTest = userRepository.save(userForTokenFailureTest);
    }

    // 1. 비밀번호 변경 (changePassword) 테스트
    @Test
    @DisplayName("로그인 상태에서 비밀번호 변경에 성공해야 한다")
    void changePassword_Success() {

        // GIVEN
        String newPassword = "newPassword456!!";
        UserPasswordChangeRequest request = new UserPasswordChangeRequest(OLD_PASSWORD, newPassword);

        // WHEN
        userCommandService.changePassword(testUser.getId(), request);

        // THEN
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        // 새 비밀번호로 로그인 가능한지 검증
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()),
                "새 비밀번호가 성공적으로 해싱되어 저장되어야 합니다.");
    }

    @Test
    @DisplayName("기존 비밀번호가 일치하지 않으면 비밀번호 변경에 실패해야 한다")
    void changePassword_Failure_MismatchedPassword() {

        // GIVEN
        String wrongPassword = "wrongPassword";
        String newPassword = "newPassword456!!";
        UserPasswordChangeRequest request = new UserPasswordChangeRequest(wrongPassword, newPassword);

        // WHEN & THEN
        assertThrows(RuntimeException.class,
                () -> userCommandService.changePassword(testUser.getId(), request),
                "기존 비밀번호가 틀리면 예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("새 비밀번호가 현재 비밀번호와 같으면 변경에 실패해야 한다")
    void changePassword_Failure_SamePassword() {

        // GIVEN
        UserPasswordChangeRequest request = new UserPasswordChangeRequest(OLD_PASSWORD, OLD_PASSWORD);

        // WHEN & THEN
        assertThrows(RuntimeException.class,
                () -> userCommandService.changePassword(testUser.getId(), request),
                "새 비밀번호가 현재 비밀번호와 같으면 변경에 실패해야 합니다.");
    }

    // 2. 비밀번호 재설정 (resetPassword) 테스트
    @Test
    @DisplayName("유효한 토큰으로 비밀번호 재설정에 성공하고 토큰이 즉시 만료 처리되어야 한다")
    void resetPassword_Success() {

        // GIVEN
        String newPassword = "resetPassword789@@";
        UserPasswordResetRequest request = new UserPasswordResetRequest(TEST_EMAIL, VALID_TOKEN, newPassword);

        // WHEN
        userCommandService.resetPassword(request);

        // THEN
        // 1. 비밀번호가 성공적으로 변경되었는지 검증
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()),
                "재설정된 새 비밀번호가 성공적으로 해싱되어 저장되어야 합니다.");

        // 2. 토큰의 만료 시간이 현재 시간으로 즉시 변경되었는지 검증
        Optional<PasswordResetToken> usedTokenOpt = tokenRepository.findByToken(VALID_TOKEN);
        assertTrue(usedTokenOpt.isPresent(), "토큰이 DB에 존재해야 합니다.");
        assertTrue(usedTokenOpt.get().isExpired(),
                "비밀번호 재설정 후 토큰은 markAsUsed()에 의해 즉시 만료된 것으로 처리되어야 합니다.");
    }

    @Test
    @DisplayName("만료된 토큰으로 재설정 시 예외가 발생해야 한다")
    void resetPassword_Failure_ExpiredToken() {

        // GIVEN
        String expiredTokenString = "expired-token-for-failure";

        PasswordResetToken expiredToken = PasswordResetToken.builder()
                .userId(userForTokenFailureTest.getId())
                .token(expiredTokenString)
                .expiryDate(LocalDateTime.now().minusMinutes(1))
                .build();
        tokenRepository.save(expiredToken);

        UserPasswordResetRequest request = new UserPasswordResetRequest(userForTokenFailureTest.getEmail(), expiredTokenString, "anyNewPassword");

        // WHEN & THEN
        assertThrows(RuntimeException.class,
                () -> userCommandService.resetPassword(request),
                "만료된 토큰 사용 시 예외가 발생해야 합니다.");
    }

    // 3. 프로필 업데이트 (updateProfile) 테스트
    @Test
    @DisplayName("일반 사용자가 주소 업데이트에 성공해야 한다")
    void updateProfile_User_Success_Address() {

        // GIVEN
        String newAddress = "서울시 강남구 역삼동";
        UserUpdateProfileRequest request = new UserUpdateProfileRequest(newAddress, null);

        // WHEN
        userCommandService.updateProfile(testUser.getId(), request);

        // THEN
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(newAddress, updatedUser.getAddress(), "주소가 성공적으로 업데이트되어야 합니다.");
        assertNull(updatedUser.getCompany(), "일반 사용자는 회사명은 변경되지 않아야 합니다.");
    }

    @Test
    @DisplayName("관리자가 회사명 업데이트에 성공해야 한다")
    void updateProfile_Admin_Success_Company() {
        // GIVEN
        String newCompany = "이토피아 본사";
        // UserUpdateProfileRequest: (address, company)
        UserUpdateProfileRequest request = new UserUpdateProfileRequest(null, newCompany);

        // WHEN
        // 이 테스트가 GlobalException으로 실패했습니다. Service 구현체 확인이 필요합니다.
        userCommandService.updateProfile(adminUser.getId(), request);

        // THEN
        User updatedUser = userRepository.findById(adminUser.getId()).orElseThrow();
        assertEquals(newCompany, updatedUser.getCompany(), "관리자는 회사명이 성공적으로 업데이트되어야 합니다.");
    }

    // 4. 비밀번호 재설정 이메일 (newPasswordForEmail) 테스트
    @Test
    @DisplayName("이메일로 비밀번호 재설정 요청 시 토큰이 생성되고 메일이 발송되어야 한다")
    void newPasswordForEmail_Success() {

        // GIVEN
        UserMailRequest request = new UserMailRequest(userForTokenTest.getEmail());

        // WHEN
        userCommandService.newPasswordForEmail(request);

        // THEN
        // 1. 토큰이 DB에 저장되었는지 검증 (유저 ID로 조회)
        Optional<PasswordResetToken> newTokenOpt = tokenRepository.findByUserId(userForTokenTest.getId());
        assertTrue(newTokenOpt.isPresent(), "새로운 재설정 토큰이 DB에 저장되어야 합니다.");

        String generatedToken = newTokenOpt.get().getToken();

        // 2. Mocking된 MailService의 sendMail 메서드가 생성된 정확한 토큰 값으로 호출되었는지 검증
        verify(mailService, times(1)).sendPasswordResetMail(
                eq(userForTokenTest.getEmail()), // 토큰 발송 유저의 이메일
                eq(generatedToken) // DB에서 조회한 정확한 토큰 값으로 검증
        );
    }
}

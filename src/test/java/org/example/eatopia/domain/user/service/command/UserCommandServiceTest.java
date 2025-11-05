package org.example.eatopia.domain.user.service.command;

import org.example.eatopia.common.core.exception.GlobalException;
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
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class UserCommandServiceTest {

    private final String TEST_EMAIL = "test@eatopia.com";
    private final String ADMIN_EMAIL = "admin@eatopia.com";
    private final String OLD_PASSWORD = "oldPassword123!";
    private final String VALID_TOKEN = "valid-test-token";
    private final String TEST_NAME = "테스트 사용자";
    private User testUser;
    private User adminUser;
    private User userForTokenTest;
    private User userForTokenFailureTest;

    @Autowired
    private UserCommandService userCommandService;

    // 실제 DB와 상호작용하는 Repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mocking이 필요한 빈 테스트 격리 및 인프라 오류 회피
    @MockitoBean
    private MailService mailService;
    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;
    @MockitoBean
    private CacheManager cacheManager;
    @MockitoBean
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() throws Exception {

        // CacheManager Mocking 설정 NPE 및 Redis 연결 오류 방지
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache(eq("users"))).thenReturn(mockCache);
        doReturn(true).when(mockCache).evictIfPresent(any());

        // 1. 테스트 사용자 생성 및 DB 저장 실제 ID 할당
        testUser = User.signUp(TEST_EMAIL, passwordEncoder.encode(OLD_PASSWORD), TEST_NAME, UserRole.BUYER);
        testUser = userRepository.save(testUser);

        adminUser = User.signUp(ADMIN_EMAIL, passwordEncoder.encode(OLD_PASSWORD), "관리자", UserRole.ADMIN);
        adminUser = userRepository.save(adminUser);

        userForTokenTest = User.signUp("token@test.com", passwordEncoder.encode(OLD_PASSWORD), "토큰 발송 유저", UserRole.BUYER);
        userForTokenTest = userRepository.save(userForTokenTest);

        userForTokenFailureTest = User.signUp("failure@test.com", passwordEncoder.encode(OLD_PASSWORD), "토큰 실패 유저", UserRole.BUYER);
        userForTokenFailureTest = userRepository.save(userForTokenFailureTest);

        // 3. 유효한 비밀번호 재설정 토큰 생성 및 저장
        PasswordResetToken token = PasswordResetToken.builder()
                .userId(testUser.getId())
                .token(VALID_TOKEN)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        tokenRepository.save(token);

        // 4. UserQueryService Mocking 보강 NPE 방지
        // changePassword, updateProfile 등에서 호출되는 ID/Email 기반 조회에 대한 Mocking
        when(userQueryService.getActiveUserById(eq(testUser.getId()))).thenReturn(testUser);
        when(userQueryService.getActiveUserById(eq(adminUser.getId()))).thenReturn(adminUser);
        when(userQueryService.getActiveUserById(eq(userForTokenTest.getId()))).thenReturn(userForTokenTest);
        when(userQueryService.getActiveUserById(eq(userForTokenFailureTest.getId()))).thenReturn(userForTokenFailureTest);

        when(userQueryService.getActiveUserByEmail(eq(TEST_EMAIL))).thenReturn(testUser);
        when(userQueryService.getActiveUserByEmail(eq(ADMIN_EMAIL))).thenReturn(adminUser);
        when(userQueryService.getActiveUserByEmail(eq(userForTokenTest.getEmail()))).thenReturn(userForTokenTest);
        when(userQueryService.getActiveUserByEmail(eq(userForTokenFailureTest.getEmail()))).thenReturn(userForTokenFailureTest);

        // Cache 제거 로직 evictUserCache에서 호출되는 getUserEntityById Mocking
        when(userQueryService.getUserEntityById(eq(testUser.getId()))).thenReturn(testUser);
        when(userQueryService.getUserEntityById(eq(adminUser.getId()))).thenReturn(adminUser);
    }

    // 1. 비밀번호 변경 changePassword 테스트
    @Test
    @DisplayName("로그인 상태에서 비밀번호 변경에 성공해야 한다")
    void changePassword_Success() {

        // GIVEN
        String newPassword = "newPassword456!!";
        UserPasswordChangeRequest request = new UserPasswordChangeRequest(OLD_PASSWORD, newPassword);
        Cache mockCache = cacheManager.getCache("users");

        // WHEN
        userCommandService.changePassword(testUser.getId(), request);

        // THEN
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()), "새 비밀번호가 성공적으로 저장되어야 합니다.");

        // 캐시 제거 검증 프로그래밍 방식
        verify(mockCache, times(1)).evictIfPresent(testUser.getId());
        verify(mockCache, times(1)).evictIfPresent(testUser.getEmail());
    }

    @Test
    @DisplayName("기존 비밀번호가 일치하지 않으면 비밀번호 변경에 실패해야 한다")
    void changePassword_Failure_MismatchedPassword() {

        // GIVEN
        String wrongPassword = "wrongPassword";
        String newPassword = "newPassword456!!";
        UserPasswordChangeRequest request = new UserPasswordChangeRequest(wrongPassword, newPassword);

        // WHEN & THEN
        assertThrows(GlobalException.class,
                () -> userCommandService.changePassword(testUser.getId(), request),
                "기존 비밀번호가 틀리면 예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("새 비밀번호가 현재 비밀번호와 같으면 변경에 실패해야 한다")
    void changePassword_Failure_SamePassword() {

        // GIVEN
        UserPasswordChangeRequest request = new UserPasswordChangeRequest(OLD_PASSWORD, OLD_PASSWORD);

        // WHEN & THEN
        assertThrows(GlobalException.class,
                () -> userCommandService.changePassword(testUser.getId(), request),
                "새 비밀번호가 현재 비밀번호와 같으면 변경에 실패해야 합니다.");
    }

    // 2. 비밀번호 재설정 resetPassword 테스트
    @Test
    @DisplayName("유효한 토큰으로 비밀번호 재설정에 성공하고 토큰이 즉시 만료 처리되어야 한다")
    void resetPassword_Success() {

        // GIVEN
        String newPassword = "resetPassword789@@";
        UserPasswordResetRequest request = new UserPasswordResetRequest(TEST_EMAIL, VALID_TOKEN, newPassword);
        Cache mockCache = cacheManager.getCache("users");

        // WHEN
        userCommandService.resetPassword(request);

        // THEN
        // 1. 비밀번호 변경 검증
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()), "비밀번호가 성공적으로 변경되어야 합니다.");

        // 2. 토큰 만료 검증
        Optional<PasswordResetToken> usedTokenOpt = tokenRepository.findByToken(VALID_TOKEN);
        assertTrue(usedTokenOpt.isPresent(), "토큰이 DB에 존재해야 합니다.");
        assertTrue(usedTokenOpt.get().isExpired(), "토큰이 markAsUsed에 의해 즉시 만료된 것으로 처리되어야 합니다.");

        // 캐시 제거 검증 프로그래밍 방식
        verify(mockCache, times(1)).evictIfPresent(testUser.getId());
        verify(mockCache, times(1)).evictIfPresent(testUser.getEmail());
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
        assertThrows(GlobalException.class,
                () -> userCommandService.resetPassword(request),
                "만료된 토큰 사용 시 예외가 발생해야 합니다.");
    }

    // 3. 프로필 업데이트 updateProfile 테스트
    @Test
    @DisplayName("일반 사용자가 주소 업데이트에 성공해야 한다")
    void updateProfile_User_Success_Address() {

        // GIVEN
        String newAddress = "서울시 강남구 역삼동";
        UserUpdateProfileRequest request = new UserUpdateProfileRequest(newAddress, null);
        Cache mockCache = cacheManager.getCache("users");

        // WHEN
        userCommandService.updateProfile(testUser.getId(), request);

        // THEN
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(newAddress, updatedUser.getAddress(), "주소가 성공적으로 업데이트되어야 합니다.");
        assertNull(updatedUser.getCompany(), "일반 사용자는 회사명은 변경되지 않아야 합니다.");

        // 캐시 제거 검증 프로그래밍 방식
        verify(mockCache, times(1)).evictIfPresent(testUser.getId());
        verify(mockCache, times(1)).evictIfPresent(testUser.getEmail());
    }

    @Test
    @DisplayName("관리자가 회사명 업데이트에 성공해야 한다")
    void updateProfile_Admin_Success_Company() {

        // GIVEN
        String newCompany = "이토피아 본사";
        UserUpdateProfileRequest request = new UserUpdateProfileRequest(null, newCompany);
        Cache mockCache = cacheManager.getCache("users");

        // WHEN
        userCommandService.updateProfile(adminUser.getId(), request);

        // THEN
        User updatedUser = userRepository.findById(adminUser.getId()).orElseThrow();
        assertEquals(newCompany, updatedUser.getCompany(), "관리자는 회사명이 성공적으로 업데이트되어야 합니다.");

        // 캐시 제거 검증 프로그래밍 방식
        verify(mockCache, times(1)).evictIfPresent(adminUser.getId());
        verify(mockCache, times(1)).evictIfPresent(adminUser.getEmail());
    }

    // 4. 비밀번호 재설정 이메일 newPasswordForEmail 테스트
    @Test
    @DisplayName("이메일로 비밀번호 재설정 요청 시 토큰이 생성되고 메일이 발송되어야 한다")
    void newPasswordForEmail_Success() {

        // GIVEN
        UserMailRequest request = new UserMailRequest(userForTokenTest.getEmail());

        // WHEN
        userCommandService.newPasswordForEmail(request);

        // THEN
        Optional<PasswordResetToken> newTokenOpt = tokenRepository.findByUserId(userForTokenTest.getId());
        assertTrue(newTokenOpt.isPresent(), "새로운 재설정 토큰이 DB에 저장되어야 합니다.");

        String generatedToken = newTokenOpt.get().getToken();

        // Mocking된 MailService의 sendMail 메서드가 생성된 정확한 토큰 값으로 호출되었는지 검증
        verify(mailService, times(1)).sendPasswordResetMail(
                eq(userForTokenTest.getEmail()),
                eq(generatedToken)
        );
    }

    @Test
    @DisplayName("구매자 BUYER가 회사명 필드를 전송하면 업데이트에 실패해야 한다")
    void updateProfile_Buyer_Failure_CompanyInput() {

        // GIVEN
        String attemptCompany = "금지된 회사명";
        UserUpdateProfileRequest request = new UserUpdateProfileRequest("주소", attemptCompany);

        // WHEN & THEN
        assertThrows(GlobalException.class,
                () -> userCommandService.updateProfile(testUser.getId(), request),
                "구매자가 회사명을 입력하면 예외가 발생해야 합니다.");
    }
}
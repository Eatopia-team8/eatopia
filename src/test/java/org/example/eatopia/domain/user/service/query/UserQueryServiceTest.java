package org.example.eatopia.domain.user.service.query;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.dto.response.UserDetailResponse;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserQueryServiceTest {

    private final String ACTIVE_EMAIL = "active@test.com";
    private final String DELETED_EMAIL = "deleted@test.com";
    private final String ADMIN_EMAIL = "admin@test.com";
    private final String SELLER_EMAIL = "seller@test.com";
    private User activeUser;
    private User deletedUser;
    private User adminUser;
    private User searchUser1;
    private User searchUser2;
    private User sellerUser;

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        //1. 사용자생성
        activeUser = User.signUp(
                ACTIVE_EMAIL,
                passwordEncoder.encode("password123"),
                "구매자",
                UserRole.BUYER
        );
        activeUser = userRepository.save(activeUser);

        //1-2 판매자생성
        sellerUser = User.signUp(
                SELLER_EMAIL,
                passwordEncoder.encode("password123"),
                "판매자",
                UserRole.SELLER
        );
        sellerUser = userRepository.save(sellerUser);

        //2. 관리자생성
        adminUser = User.signUp(
                ADMIN_EMAIL,
                passwordEncoder.encode("password123"),
                "관리자",
                UserRole.ADMIN
        );
        adminUser = userRepository.save(adminUser);

        //3. 삭제된 구매자
        deletedUser = User.signUp(
                DELETED_EMAIL,
                passwordEncoder.encode("password123"),
                "삭제된 구매자",
                UserRole.BUYER
        );
        deletedUser.softDelete();
        deletedUser = userRepository.save(deletedUser);

        //4. 검색테스트용 사용자
        searchUser1 = User.signUp(
                "search1@test.com",
                passwordEncoder.encode("password123"),
                "김철수",
                UserRole.BUYER
        );
        userRepository.save(searchUser1);

        searchUser2 = User.signUp(
                "search2@test.com",
                passwordEncoder.encode("password123"),
                "박철수",
                UserRole.BUYER
        );
        userRepository.save(searchUser2);
    }

    //1. ID로 조회테스트
    @Test
    @DisplayName("ID로 사용자상세정보 조회시 DTO와 UserRole이 정확히 반환되어야 한다")
    void getUserById_Success() {

        //when
        UserDetailResponse response = userQueryService.getUserById(activeUser.getId());

        //then
        assertNotNull(response);
        assertEquals(activeUser.getId(), response.id());
        assertEquals(activeUser.getEmail(), response.email());
        assertEquals(UserRole.BUYER, response.role());
    }

    @Test
    @DisplayName("존재하지않는 ID조회시 GlobalException이 발생해야 한다")
    void getUserById_NotFound() {

        //given
        Long nonExistentId = 9999L;

        //when & then
        assertThrows(GlobalException.class,
                () -> userQueryService.getUserById(nonExistentId),
                "존재하지않는 ID조회시 GlobalException이 발생해야 한다"
        );
    }

    //2. 사용자조회 테스트
    @Test
    @DisplayName("활성 사용자는 ID로 조회에 성공하고 엔티티가 반환되어야 한다")
    void getActiveUserById_Success() {

        //when
        User foundUser = userQueryService.getActiveUserById(activeUser.getId());

        //then
        assertNotNull(foundUser);
        assertNull(foundUser.getDeletedAt(), "활성 사용자는 deletedAt 필드가 null이어야 합니다");
        assertEquals(UserRole.BUYER, foundUser.getUserRole());
    }

    @Test
    @DisplayName("삭제된 사용자 ID로 조회 시 GlobalException이 발생해야 한다")
    void getActiveUserById_DeletedUser() {

        //when & then
        assertThrows(GlobalException.class,
                () -> userQueryService.getActiveUserById(deletedUser.getId()),
                "삭제된 사용자 ID로 조회 시 GlobalException이 발생해야 한다");
    }

    @Test
    @DisplayName("활성 사용자는 Email로 조회에 성공하고 엔티티가 반환되어야한다")
    void getActiveUserByEmail_Success() {

        //when
        User foundUser = userQueryService.getActiveUserByEmail(ACTIVE_EMAIL);

        //then
        assertNotNull(foundUser);
        assertEquals(ACTIVE_EMAIL, foundUser.getEmail());
        assertEquals(UserRole.BUYER, foundUser.getUserRole());
    }

    //3. 관리자기능 테스트
    @Test
    @DisplayName("관리자는 전체 사용자 목록을 페이지네이션하여 조회해야 한다")
    void getAllUsersForAdmin_Success() {

        //given
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<UserDetailResponse> resultPage = userQueryService.getAllUsersForAdmin(adminUser.getId(), pageable);

        //then
        assertFalse(resultPage.isEmpty());
        assertTrue(resultPage.getTotalElements() >= 5, "최소 5명 이상의 활성 사용자가 조회되어야 합니다.");
    }

    @Test
    @DisplayName("일반 사용자(BUYER)가 전체 사용자 목록 조회 시 권한 예외가 발생해야 한다")
    void getAllUsersForAdmin_AccessDenied() {

        //given
        Pageable pageable = PageRequest.of(0, 10);

        //when & then
        assertThrows(GlobalException.class,
                () -> userQueryService.getAllUsersForAdmin(adminUser.getId(), pageable),
                "관리자가 아닌 사용자가 전체 목록 조회 시 Access Denied 예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("관리자는 키워드로 사용자 검색에 성공해야 한다")
    void searchUsers_Success() {

        //given
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "철수";

        //when
        Page<UserDetailResponse> resultPage = userQueryService.searchUsers(activeUser.getId(), keyword, pageable);

        //then
        assertEquals(2, resultPage.getTotalElements());
        assertTrue(resultPage.getContent().stream().anyMatch(u -> u.name().equals("김철수")));

    }
}
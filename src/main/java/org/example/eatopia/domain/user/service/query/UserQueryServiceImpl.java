package org.example.eatopia.domain.user.service.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.user.dto.response.UserDetailResponse;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    // ID로 활성사용자를 조회하고 탈퇴여부 검사
    public User getActiveUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, userId));

        if (user.getDeletedAt() != null) {
            throw new GlobalException(AuthErrorCode.USER_IS_DELETED);
        }
        return user;
    }

    // Email로 활성사용자를 조회하고 탈퇴여부 검사
    public User getActiveUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, email));

        // 탈퇴 상태 확인 로직
        if (user.getDeletedAt() != null) {
            throw new GlobalException(AuthErrorCode.USER_IS_DELETED);
        }
        return user;
    }

    @Override
    public Page<UserDetailResponse> searchUsers(Long requestingUserId, String keyword, Pageable pageable) {

        //1.Admin인지 확인
        User requester = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, requestingUserId));

        //2. Admin역할이 아니면 ACCESS_DENIED오류 발생
        if (!requester.isAdmin()) {
            throw new GlobalException(UserErrorCode.ACCESS_DENIED);
        }

        // 3. 검색어(keyword) 공백 제거
        String trimmedKeyword = StringUtils.trimToEmpty(keyword);

        // 4. 권한 검사 통과 후, 이메일/이름 통합 검색
        Page<UserDetailResponse> usersPage = userRepository.searchByEmailOrNameAndNotDeleted(trimmedKeyword, pageable)
                .map(UserDetailResponse::of);

        // 5. 중간 변수를 사용하여 반환
        return usersPage;
    }

    @Override
    public UserDetailResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, userId));

        return UserDetailResponse.of(user);
    }

    @Override
    public User getUserEntityById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, userId));
    }

    @Override
    public Page<UserDetailResponse> getAllUsersForAdmin(Long requestingUserId, Pageable pageable) {

        //1. 요청사용자(Admin) 조회
        User requester = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, requestingUserId));

        //2. Admin역할이 아니면 ACCESS_DENIED 오류 발생
        if (!requester.getUserRole().name().equals("ADMIN")) {
            throw new GlobalException(UserErrorCode.ACCESS_DENIED);
        }

        return userRepository.findAll(pageable)
                .map(UserDetailResponse::of);
    }
}
package org.example.eatopia.domain.user.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.user.dto.UserDetailResponse;
import org.example.eatopia.domain.user.enttiy.User;
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

    @Override
    public UserDetailResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND, userId));

        return UserDetailResponse.of(user);
    }

    @Override
    public Page<UserDetailResponse> getAllUsers(Pageable pageable) {
        // JPA Repository의 findAll(Pageable)을 사용하여 DB에서 페이지 단위로 조회
        return userRepository.findAll(pageable)
                .map(UserDetailResponse::of);
    }
}
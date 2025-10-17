package org.example.eatopia.domain.user.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.user.dto.UserDetailResponse;
import org.example.eatopia.domain.user.enttiy.User;
import org.example.eatopia.domain.user.exception.UserErrorCode;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public UserDetailResponse getUserById(Long userId) {
        //ID로 사용자 조회 없으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));
        return UserDetailResponse.of(user);
    }

    @Override
    public List<UserDetailResponse> getAllUsers() {
        //모든 사용자 정보를 조회하여 DTO목록으로 변환
        return userRepository.findAll().stream()
                .map(UserDetailResponse::of)
                .collect(Collectors.toList());
    }

}

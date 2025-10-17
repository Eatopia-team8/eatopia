package org.example.eatopia.domain.user.service.query;

import org.example.eatopia.domain.user.dto.UserDetailResponse;

import java.util.List;

public interface UserQueryService {
    /**
     * ID로 사용자 상세 정보를 조회
     */
    UserDetailResponse getUserById(Long userId);

    /**
     * 전체 사용자 목록을 조회
     */
    List<UserDetailResponse> getAllUsers();
}

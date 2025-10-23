package org.example.eatopia.domain.user.service.query;

import org.example.eatopia.domain.user.dto.response.UserDetailResponse;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryService {

    //ID로 사용자 상세 정보를 조회
    UserDetailResponse getUserById(Long userId);

    //ID로 사용자 엔티티를 조회하여 반환
    User getUserEntityById(Long userId);

    //관리자기능 - 전체 사용자 목록을 페이지네이션하여 조회합니다.
    Page<UserDetailResponse> getAllUsersForAdmin(Long requestingUserId, Pageable pageable);

    //ID로 활성사용자를 조회하고 탈퇴여부를 검사
    User getActiveUserById(Long userId);

    //Email로 활성사용자를 조회하고 탈퇴여부를 검사
    User getActiveUserByEmail(String email);

    //이메일 또는 이름으로 사용자 검색
    Page<UserDetailResponse> searchUsers(Long requestingUserId, String keyword, Pageable pageable);

}

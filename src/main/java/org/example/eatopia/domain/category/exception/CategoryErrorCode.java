package org.example.eatopia.domain.category.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {

    CTG_NAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "CTG-001", "이미 존재하는 카테고리 이름입니다."),
    CTG_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "CTG-002", "해당 카테고리를 찾을 수 없습니다."),
    CTG_HAS_CHILDREN(HttpStatus.BAD_REQUEST, "CTG-003", "하위 카테고리가 있어 삭제할 수 없습니다."),
    CTG_INVALID_PARENT(HttpStatus.BAD_REQUEST, "CTG-004", "자기 자신을 상위 카테고리로 설정할 수 없습니다."),
    CTG_DEPTH1_CANNOT_HAVE_PARENT(HttpStatus.BAD_REQUEST, "CTG-005", "상위 카테고리는 하위 카테고리로 설정할 수 없습니다."),
    CTG_INVALID_PARENT_DEPTH(HttpStatus.BAD_REQUEST, "CTG-006", "하위 카테고리 밑에는 카테고리를 추가할 수 없습니다."),
    CTG_CANNOT_REMOVE_PARENT(HttpStatus.BAD_REQUEST, "CTG-007", "하위 카테고리는 상위 카테고리가 될 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

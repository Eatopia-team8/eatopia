package org.example.eatopia.domain.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatRoomErrorCode implements ErrorCode {

    SAME_USER(HttpStatus.BAD_REQUEST, "CHR-001", "동일 유저에 대한 채팅방은 생성할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

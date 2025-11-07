package org.example.eatopia.domain.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatMessageErrorCode implements ErrorCode {

    CHAT_MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "CHM-001", "채팅 메시지는 비어 있을 수 없습니다."),
    CHAT_MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "CHM-002", "채팅 메시지가 허용된 최대 길이를 초과했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

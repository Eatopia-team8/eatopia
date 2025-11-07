package org.example.eatopia.domain.chat.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class ChatMessageException extends GlobalException {
    public ChatMessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package org.example.eatopia.domain.chat.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class ChatRoomException extends GlobalException {
    public ChatRoomException(ErrorCode errorCode) {
        super(errorCode);
    }
}

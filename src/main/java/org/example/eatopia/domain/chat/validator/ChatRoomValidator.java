package org.example.eatopia.domain.chat.validator;

import org.example.eatopia.domain.chat.exception.ChatRoomErrorCode;
import org.example.eatopia.domain.chat.exception.ChatRoomException;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomValidator {

    public void validateDifferentUsers(User user1, User user2) {

        if (user1.getId().equals(user2.getId())) {
            throw new ChatRoomException(ChatRoomErrorCode.SAME_USER);
        }
    }
}

package org.example.eatopia.domain.chat.service.chatRoom;

import org.example.eatopia.domain.chat.dto.response.ChatRoomResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;

public interface ChatRoomCommandService {

    ChatRoomResponse getOrCreateChatRoom(UserPrincipal authUser, Long recipientId);
}

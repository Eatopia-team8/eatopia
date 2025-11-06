package org.example.eatopia.domain.chat.service.chatRoom;

import org.example.eatopia.domain.chat.dto.response.ChatRoomResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;

import java.util.List;

public interface ChatRoomQueryService {

    List<ChatRoomResponse> getChatRooms(UserPrincipal authUser);
}
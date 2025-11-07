package org.example.eatopia.domain.chat.service.chatRoom;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.chat.dto.response.ChatRoomResponse;
import org.example.eatopia.domain.chat.entity.ChatRoom;
import org.example.eatopia.domain.chat.exception.ChatRoomErrorCode;
import org.example.eatopia.domain.chat.exception.ChatRoomException;
import org.example.eatopia.domain.chat.repository.ChatRoomRepository;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomQueryServiceImpl implements ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserQueryService userQueryService;

    public List<ChatRoomResponse> getChatRooms(UserPrincipal authUser) {

        User user = userQueryService.getUserEntityById(authUser.getId());

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUser(user);

        return chatRooms.stream().map(ChatRoomResponse::from).toList();
    }

    public ChatRoom getChatRoomById(Long roomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        return chatRoom;
    }
}
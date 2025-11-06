package org.example.eatopia.domain.chat.service.chatRoom;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.chat.dto.response.ChatRoomResponse;
import org.example.eatopia.domain.chat.entity.ChatRoom;
import org.example.eatopia.domain.chat.repository.ChatRoomRepository;
import org.example.eatopia.domain.chat.validator.ChatRoomValidator;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomCommandServiceImpl implements ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserQueryService userQueryService;
    private final ChatRoomValidator chatRoomValidator;

    public ChatRoomResponse getOrCreateChatRoom(UserPrincipal authUser, Long recipientId) {

        User recipientUser = userQueryService.getUserEntityById(recipientId);
        User sendUser = userQueryService.getUserEntityById(authUser.getId());

        chatRoomValidator.validateDifferentUsers(recipientUser, sendUser);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByParticipants(recipientUser, sendUser).orElseGet(() -> {

            ChatRoom newChatRoom = new ChatRoom(recipientUser, sendUser);

            return newChatRoom;
        });

        chatRoomRepository.save(chatRoom);

        return ChatRoomResponse.from(chatRoom);
    }
}

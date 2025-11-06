package org.example.eatopia.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.chat.dto.response.ChatRoomResponse;
import org.example.eatopia.domain.chat.service.ChatRoomCommandService;
import org.example.eatopia.domain.chat.service.ChatRoomQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ChatRoomController {

    private final ChatRoomCommandService chatRoomCommandService;
    private final ChatRoomQueryService chatRoomQueryService;

    @PostMapping("/v1/chatrooms/{recipientId}")
    public Response<ChatRoomResponse> getOrCreateChatRoom(@AuthenticationPrincipal UserPrincipal authUser,
                                                          @PathVariable Long recipientId) {

        ChatRoomResponse response = chatRoomCommandService.getOrCreateChatRoom(authUser, recipientId);

        return Response.success(response);
    }

    @GetMapping("/v1/chatrooms")
    public Response<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal UserPrincipal authUser) {

        List<ChatRoomResponse> responses = chatRoomQueryService.getChatRooms(authUser);

        return Response.success(responses);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserPrincipal authUser
            // @RequestParam(defaultValue = "0") int page
    ) {
        // TODO: chatRoomService.getMessages(roomId, userDetails.getUser(), page);
        return ResponseEntity.ok().build(); // 임시
    }
}
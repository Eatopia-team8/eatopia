package org.example.eatopia.domain.chat.repository;

import org.example.eatopia.domain.chat.entity.ChatMessage;
import org.example.eatopia.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm " +
            "JOIN FETCH cm.sender s " +
            "WHERE cm.chatRoom = :chatRoom AND (:cursorId IS NULL OR cm.id < :cursorId) " +
            "ORDER BY cm.id DESC")
    Slice<ChatMessage> findByChatRoomAndIdLessThanOrderByIdDesc(@Param("chatRoom") ChatRoom chatRoom,
                                                                @Param("cursorId") Long cursorId,
                                                                Pageable pageable);
}
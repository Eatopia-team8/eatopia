package org.example.eatopia.domain.chat.repository;

import org.example.eatopia.domain.chat.entity.ChatRoom;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE (cr.participant1 = :user1 AND cr.participant2 = :user2) " +
            "OR (cr.participant1 = :user2 AND cr.participant2 = :user1)")
    Optional<ChatRoom> findChatRoomByParticipants(@Param("user1") User user1,
                                                  @Param("user2") User user2);

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE cr.participant1 = :user " +
            "OR cr.participant2 = :user")
    List<ChatRoom> findAllByUser(@Param("user") User user);
}
package com.example.api.repositories;

import com.example.api.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByRoom_Id(Integer roomId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.room.id = :roomId AND m.user.id != :userId")
    void markAllMessagesAsReadInRoom(@Param("roomId") Integer roomId, @Param("userId") Integer userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.room.id = :roomId AND m.user.id != :userId AND m.isRead = false")
    int countUnreadMessagesForUserAndRoom(@Param("userId") Integer userId, @Param("roomId") Integer roomId);
}
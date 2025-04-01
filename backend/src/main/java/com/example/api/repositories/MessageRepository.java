package com.example.api.repositories;

import com.example.api.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserId(Integer userId);
    List<Message> findByRoomId(Long roomId);
}
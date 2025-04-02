package com.example.api.dtos;

import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    private String message;
    private Long userId;
    private Long roomId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeurs
    public MessageDto() {}

    public MessageDto(Long id, String message, Long userId, Long roomId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.message = message;
        this.userId = userId;
        this.roomId = roomId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

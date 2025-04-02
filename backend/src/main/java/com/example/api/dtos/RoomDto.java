package com.example.api.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class RoomDto {
    private Integer id;
    private String name;
    private Integer creator;
    private Integer roomType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Integer> messageIds;
    private List<Integer> userIds;

    // Constructeurs
    public RoomDto() {}

    public RoomDto(Integer id, String name, Integer creator, Integer roomType, LocalDateTime createdAt, LocalDateTime updatedAt, List<Integer> messageIds, List<Integer> userIds) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.roomType = roomType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messageIds = messageIds;
        this.userIds = userIds;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getRoomType() {
        return roomType;
    }

    public void setRoomType(Integer roomType) {
        this.roomType = roomType;
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

    public List<Integer> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<Integer> messageIds) {
        this.messageIds = messageIds;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }   
}

package com.example.api.dtos;

import com.example.api.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.hibernate.Hibernate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Integer id;
    private String content;
    private Integer userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer roomId;
    
    public static MessageDto fromEntity(Message message) {
        try {
            Integer userId = null;
            String username = null;
            Integer roomId = null;
            
            // Vérifiez si les proxies sont initialisés avant d'y accéder
            if (message.getUser() != null && Hibernate.isInitialized(message.getUser())) {
                userId = message.getUser().getId();
                username = message.getUser().getUsername();
            }
            
            if (message.getRoom() != null && Hibernate.isInitialized(message.getRoom())) {
                roomId = message.getRoom().getId();
            }
            
            return MessageDto.builder()
                    .id(message.getId())
                    .content(message.getContent())
                    .userId(userId)
                    .username(username)
                    .createdAt(message.getCreatedAt())
                    .updatedAt(message.getUpdatedAt())
                    .roomId(roomId)
                    .build();
        } catch (Exception e) {
            System.err.println("Error accessing properties: " + e.getMessage());
            return MessageDto.builder()
                    .id(message.getId())
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .updatedAt(message.getUpdatedAt())
                    .build();
        }
    }
}

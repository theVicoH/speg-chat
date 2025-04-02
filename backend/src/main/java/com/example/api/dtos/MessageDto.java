package com.example.api.dtos;

import com.example.api.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long roomId;
    
    public static MessageDto fromEntity(Message message) {
        MessageDto dto = MessageDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .roomId(message.getRoomId())
                .build();
        
        // Handle lazy loading of user
        if (message.getUser() != null) {
            try {
                dto.setUserId(message.getUser().getId().longValue());
                dto.setUsername(message.getUser().getUsername());
            } catch (Exception e) {
                // Log the error but don't fail the conversion
                System.err.println("Error accessing user properties: " + e.getMessage());
            }
        }
        
        return dto;
    }
}

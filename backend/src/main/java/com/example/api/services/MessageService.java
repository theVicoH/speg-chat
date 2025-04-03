package com.example.api.services;

import com.example.api.dtos.MessageDto;
import com.example.api.dtos.MessageRequest;
import com.example.api.entities.Message;
import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repositories.MessageRepository;
import com.example.api.repositories.RoomRepository;
import com.example.api.repositories.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    @Transactional(readOnly = true)  // Ajoutez cette annotation
    public List<MessageDto> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    public MessageDto getMessageById(Integer id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return MessageDto.fromEntity(message);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByRoomId(Integer roomId) {
        // Utiliser la méthode qui utilise la relation directe
        List<Message> messages = messageRepository.findByRoom_Id(roomId);
        System.out.println("Found " + messages.size() + " messages for room ID: " + roomId);
        
        return messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    public MessageDto createMessage(MessageRequest messageRequest) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Récupérer la room par ID
        Room room = roomRepository.findById(messageRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + messageRequest.getRoomId()));
        
        Message message = Message.builder()
                .content(messageRequest.getContent())
                .user(currentUser)
                .room(room) // Utiliser la relation room au lieu de roomId
                .build();
        
        Message savedMessage = messageRepository.save(message);
        return MessageDto.fromEntity(savedMessage);
    }

    public MessageDto updateMessage(Integer id, MessageRequest messageRequest) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        
        // Check if the current user is the owner of the message
        if (!message.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own messages");
        }
        
        // Récupérer la room par ID si nécessaire
        if (messageRequest.getRoomId() != null) {
            Room room = roomRepository.findById(messageRequest.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + messageRequest.getRoomId()));
            message.setRoom(room);
        }
        
        message.setContent(messageRequest.getContent());
        
        Message updatedMessage = messageRepository.save(message);
        return MessageDto.fromEntity(updatedMessage);
    }

    public void deleteMessage(Integer id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        
        // Get the room ID from the message
        Integer roomId = message.getRoom().getId();
        Integer messageOwnerId = message.getUser().getId();
        
        // Check if the current user is the owner of the message
        boolean isMessageOwner = messageOwnerId.equals(currentUser.getId());
        
        // Check if the current user is an admin or moderator in the room
        boolean isRoomAdmin = isUserRoomAdmin(currentUser.getId(), roomId);
        boolean isRoomModerator = isUserRoomModerator(currentUser.getId(), roomId);
        
        // Check if the message owner is an admin (moderators can't delete admin messages)
        boolean isMessageOwnerAdmin = isUserRoomAdmin(messageOwnerId, roomId);
        
        // Allow deletion if:
        // 1. User is the message owner, OR
        // 2. User is an admin in the room, OR
        // 3. User is a moderator AND the message owner is not an admin
        if (isMessageOwner || isRoomAdmin || (isRoomModerator && !isMessageOwnerAdmin)) {
            messageRepository.delete(message);
        } else {
            throw new IllegalStateException("You don't have permission to delete this message");
        }
    }
    
    // Helper method to check if a user is an admin in a room
    private boolean isUserRoomAdmin(Integer userId, Integer roomId) {
        try {
            // Role ID 1 is for administrators according to your SQL initialization
            return userRoomRepository.findByUserIdAndRoomIdAndRoleId(userId, roomId, 1).isPresent();
        } catch (Exception e) {
            // If there's an error, assume the user is not an admin
            return false;
        }
    }
    
    // Helper method to check if a user is a moderator in a room
    private boolean isUserRoomModerator(Integer userId, Integer roomId) {
        try {
            // Role ID 2 is for moderators according to your SQL initialization
            return userRoomRepository.findByUserIdAndRoomIdAndRoleId(userId, roomId, 2).isPresent();
        } catch (Exception e) {
            // If there's an error, assume the user is not a moderator
            return false;
        }
    }
}
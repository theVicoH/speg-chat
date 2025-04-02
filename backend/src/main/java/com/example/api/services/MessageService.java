package com.example.api.services;

import com.example.api.dtos.MessageDto;
import com.example.api.dtos.MessageRequest;
import com.example.api.entities.Message;
import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repositories.MessageRepository;
import com.example.api.repositories.RoomRepository;
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
        
        // Check if the current user is the owner of the message
        if (!message.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own messages");
        }
        
        messageRepository.delete(message);
    }
}
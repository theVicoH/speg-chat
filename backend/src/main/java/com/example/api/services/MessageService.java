package com.example.api.services;

import com.example.api.dtos.MessageDto;
import com.example.api.dtos.MessageRequest;
import com.example.api.entities.Message;
import com.example.api.entities.User;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public List<MessageDto> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    public MessageDto getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return MessageDto.fromEntity(message);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByRoomId(Long roomId) {
        List<Message> messages = messageRepository.findByRoomId(roomId);
        System.out.println("Found " + messages.size() + " messages for room ID: " + roomId);
        
        return messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    public MessageDto createMessage(MessageRequest messageRequest) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Message message = Message.builder()
                  .content(messageRequest.getContent())
                  .user(currentUser)
                  .roomId(messageRequest.getRoomId())
                  .build();
          
        Message savedMessage = messageRepository.save(message);
        return MessageDto.fromEntity(savedMessage);
    }

    public MessageDto updateMessage(Long id, MessageRequest messageRequest) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        
        // Check if the current user is the owner of the message
        if (!message.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own messages");
        }
        
        message.setContent(messageRequest.getContent());
        message.setRoomId(messageRequest.getRoomId());
        
        Message updatedMessage = messageRepository.save(message);
        return MessageDto.fromEntity(updatedMessage);
    }

    public void deleteMessage(Long id) {
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
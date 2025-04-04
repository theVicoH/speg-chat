package com.example.api.services;

import com.example.api.dtos.MessageDto;
import com.example.api.dtos.MessageRequest;
import com.example.api.entities.UserRoom;
import com.example.api.repositories.UserRoomRepository;
import com.example.api.entities.Message;
import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.exceptions.ResourceNotFoundException;
import com.example.api.exceptions.ForbiddenActionException;
import com.example.api.repositories.MessageRepository;
import com.example.api.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final UserRoomService userRoomService;

    public MessageDto getMessageById(Integer id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        
        // Check if the user is a member of the room where the message is
        if (!userRoomService.isUserMemberOfRoom(currentUser.getId(), message.getRoom().getId())) {
            throw new IllegalStateException("You cannot access messages in rooms you are not a member of");
        }
        
        return MessageDto.fromEntity(message);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByRoomId(Integer roomId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Check if the user is a member of the room
        if (!userRoomService.isUserMemberOfRoom(currentUser.getId(), roomId)) {
            throw new IllegalStateException("You are not a member of this room");
        }
        
        // Utiliser la méthode qui utilise la relation directe
        List<Message> messages = messageRepository.findByRoom_Id(roomId);
        System.out.println("Found " + messages.size() + " messages for room ID: " + roomId);
        
        return messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    public MessageDto createMessage(MessageRequest messageRequest) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
        if (isUserBlocked(currentUser.getId(), messageRequest.getRoomId())) {
            throw new ForbiddenActionException("You are blocked from sending messages in this room");
        }
        
        if (!userRoomService.isUserMemberOfRoom(currentUser.getId(), messageRequest.getRoomId())) {
            throw new IllegalStateException("You cannot post messages in a room you are not a member of");
        }
        
        Room room = roomRepository.findById(messageRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + messageRequest.getRoomId()));
        
        Message message = Message.builder()
                .content(messageRequest.getContent())
                .user(currentUser)
                .room(room)
                .build();
        
        Message savedMessage = messageRepository.save(message);
        return MessageDto.fromEntity(savedMessage);
    }

    public MessageDto updateMessage(Integer id, MessageRequest messageRequest) {
       User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        
        if (!message.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only update your own messages");
        }
        
        if (!userRoomService.isUserMemberOfRoom(currentUser.getId(), message.getRoom().getId())) {
            throw new IllegalStateException("You are not a member of this room");
        }

         if (isUserBlocked(currentUser.getId(), message.getRoom().getId())) {
            throw new ForbiddenActionException("You are blocked from this room");
        }
        
        if (messageRequest.getRoomId() != null && !messageRequest.getRoomId().equals(message.getRoom().getId())) {
            if (!userRoomService.isUserMemberOfRoom(currentUser.getId(), messageRequest.getRoomId())) {
                throw new IllegalStateException("You cannot move the message to a room you are not a member of");
            }
            
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
        
        if (!userRoomService.isUserMemberOfRoom(currentUser.getId(), message.getRoom().getId())) {
            throw new IllegalStateException("You are not a member of this room");
        }

          if (isUserBlocked(currentUser.getId(), message.getRoom().getId())) {
            throw new ForbiddenActionException("You are blocked  this room");
        } 
        
        if (!message.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only delete your own messages");
        }
        
        messageRepository.delete(message);
    }
    
    private boolean isUserBlocked(Integer userId, Integer roomId) {
        Optional<UserRoom> userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId);
        
        boolean isBlocked = userRoom.map(UserRoom::isBlocked).orElse(true);        
        return isBlocked;
    }
}
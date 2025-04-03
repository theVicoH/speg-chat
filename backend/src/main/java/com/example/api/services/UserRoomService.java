package com.example.api.services;

import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.entities.UserRoom;
import com.example.api.repositories.RoomRepository;
import com.example.api.repositories.UserRepository;
import com.example.api.repositories.UserRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRoomService {
    
    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    
    @Transactional
    public UserRoom joinRoom(Integer userId, Integer roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Salon non trouvé avec l'ID: " + roomId));
        
        // Vérifier si l'utilisateur est déjà dans le salon
        if (userRoomRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new IllegalStateException("L'utilisateur est déjà membre de ce salon");
        }
        
        // Par défaut, attribuer le rôle "basic" (ID 3 selon votre initialisation SQL)
        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .roleId(3) // Rôle "basic"
                .build();
        
        return userRoomRepository.save(userRoom);
    }
}
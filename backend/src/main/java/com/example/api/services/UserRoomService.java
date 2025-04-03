package com.example.api.services;

import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.entities.UserRoom;
import com.example.api.exceptions.ApiException;
import com.example.api.repositories.RoomRepository;
import com.example.api.repositories.UserRepository;
import com.example.api.repositories.UserRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            throw new ApiException("Vous êtes déjà membre de ce salon", HttpStatus.CONFLICT);
        }
        
        // Vérifier si c'est une room privée (type ID 2) et si elle a déjà 2 utilisateurs
        boolean isPrivateRoom = room.getType().getId() == 2;
        if (isPrivateRoom) {
            // Compter le nombre d'utilisateurs dans la room
            long userCount = userRoomRepository.countByRoomId(roomId);
            if (userCount >= 2) {
                throw new ApiException("Ce salon privé a déjà atteint sa limite de 2 utilisateurs", HttpStatus.FORBIDDEN);
            }
        }
        
        // Par défaut, attribuer le rôle "basic" (ID 3 selon votre initialisation SQL)
        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .roleId(3) // Rôle "basic"
                .build();
        
        return userRoomRepository.save(userRoom);
    }
    
    @Transactional
    public UserRoom joinRoomWithRole(Integer userId, Integer roomId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Salon non trouvé avec l'ID: " + roomId));
        
        // Vérifier si l'utilisateur est déjà dans le salon
        if (userRoomRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new ApiException("Vous êtes déjà membre de ce salon", HttpStatus.CONFLICT);
        }
        
        // Vérifier si c'est une room privée (type ID 2) et si elle a déjà 2 utilisateurs
        boolean isPrivateRoom = room.getType().getId() == 2;
        if (isPrivateRoom) {
            // Compter le nombre d'utilisateurs dans la room
            long userCount = userRoomRepository.countByRoomId(roomId);
            if (userCount >= 2) {
                throw new ApiException("Ce salon privé a déjà atteint sa limite de 2 utilisateurs", HttpStatus.FORBIDDEN);
            }
        }
        
        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .roleId(roleId) // Use the provided role ID
                .build();
        
        return userRoomRepository.save(userRoom);
    }
    
    @Transactional
    public void leaveRoom(Integer userId, Integer roomId) {
        // Vérifier si l'utilisateur existe
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId);
        }
        
        // Vérifier si le salon existe (using existsById instead of findById)
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Salon non trouvé avec l'ID: " + roomId);
        }
        
        // Vérifier si l'utilisateur est membre du salon
        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new IllegalStateException("L'utilisateur n'est pas membre de ce salon"));
        
        // Supprimer la relation
        userRoomRepository.delete(userRoom);
    }
}
package com.example.api.services;

import com.example.api.dtos.UserRoomDto;
import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.entities.UserRoom;
import com.example.api.exceptions.ApiException;
import com.example.api.mappers.UserRoomMapper;
import com.example.api.repositories.RoomRepository;
import com.example.api.repositories.UserRepository;
import com.example.api.repositories.UserRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import com.example.api.entities.Role;
import com.example.api.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserRoomService {
    
    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoleRepository roleRepository; // Assurez-vous d'avoir un repository pour les rôles
    private final UserRoomMapper userRoomMapper;
    
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
        Role basicRole = roleRepository.findById(3)
                .orElseThrow(() -> new EntityNotFoundException("Rôle de base non trouvé avec l'ID: 3"));

        // Créer l'entrée UserRoom avec le rôle "basic"
        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .roleId(basicRole) // Utilisation de l'objet Role, pas du roleId
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

        // Récupérer le rôle à partir de l'ID fourni
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID: " + roleId));

        // Créer l'entrée UserRoom avec le rôle spécifié
        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .roleId(role)
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

    public UserRoomDto getUserRoomByRoom(Integer roomId) {
        List<UserRoom> userRooms = userRoomRepository.findAllByRoomId(roomId);
    
        if (userRooms.isEmpty()) {
            throw new EntityNotFoundException("Aucun utilisateur trouvé pour la salle avec l'ID: " + roomId);
        }
    
        return userRoomMapper.toDto(userRooms);
    }
    
    
    @Transactional(readOnly = true)
    public boolean isUserMemberOfRoom(Integer userId, Integer roomId) {
        return userRoomRepository.existsByUserIdAndRoomId(userId, roomId);
    }
}
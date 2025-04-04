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
import org.springframework.security.access.AccessDeniedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final RoleRepository roleRepository;
    private final UserRoomMapper userRoomMapper;
    
    @Transactional
    public UserRoom joinRoom(Integer userId, Integer roomId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

    Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new EntityNotFoundException("Salon non trouvé avec l'ID: " + roomId));

        if (userRoomRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new ApiException("Vous êtes déjà membre de ce salon", HttpStatus.CONFLICT);
        }
        
        boolean isPrivateRoom = room.getType().getId() == 2;
        if (isPrivateRoom) {
            long userCount = userRoomRepository.countByRoomId(roomId);
            if (userCount >= 2) {
                throw new ApiException("Ce salon privé a déjà atteint sa limite de 2 utilisateurs", HttpStatus.FORBIDDEN);
            }
        }

        Role basicRole = roleRepository.findById(3)
                .orElseThrow(() -> new EntityNotFoundException("Rôle de base non trouvé avec l'ID: 3"));

        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .roleId(basicRole)
                .build();

    return userRoomRepository.save(userRoom);
    }

    @Transactional
    public UserRoom joinRoomWithRole(Integer userId, Integer roomId, Integer roleId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

    Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new EntityNotFoundException("Salon non trouvé avec l'ID: " + roomId));

        if (userRoomRepository.existsByUserIdAndRoomId(userId, roomId)) {
            throw new ApiException("Vous êtes déjà membre de ce salon", HttpStatus.CONFLICT);
        }
        
        boolean isPrivateRoom = room.getType().getId() == 2;
        if (isPrivateRoom) {
            long userCount = userRoomRepository.countByRoomId(roomId);
            if (userCount >= 2) {
                throw new ApiException("Ce salon privé a déjà atteint sa limite de 2 utilisateurs", HttpStatus.FORBIDDEN);
            }
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID: " + roleId));

        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .roleId(role)
                .build();

    return userRoomRepository.save(userRoom);
    }
    
    @Transactional
    public void leaveRoom(Integer userId, Integer roomId) {

        checkAdminAccess(roomId);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId);
        }
        
        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new IllegalStateException("L'utilisateur n'est pas membre de ce salon"));
        
        userRoomRepository.delete(userRoom);
    }

    public UserRoomDto getUserRoomByRoom(Integer roomId) {
        List<UserRoom> userRooms = userRoomRepository.findAllByRoomId(roomId);
    
        if (userRooms.isEmpty()) {
            throw new EntityNotFoundException("Aucun utilisateur trouvé pour la salle avec l'ID: " + roomId);
        }
    
        return userRoomMapper.toDto(userRooms);
    }

    public UserRoomDto updateUserRoomRole(Integer userId , Integer roomId, Integer roleId) {

        checkAdminAccess(roomId);

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId)
        .orElseThrow(() -> new EntityNotFoundException("L'utilisateur n'est pas membre de ce salon"));
    
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec l'ID: " + roleId));
    
        userRoom.setRoleId(role);
        UserRoom updatedUserRoom = userRoomRepository.save(userRoom);
    
        return userRoomMapper.toDto(List.of(updatedUserRoom));
    }

    public UserRoomDto blockUser(Integer userId, Integer roomId) {
        checkAdminAccess(roomId);

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur n'est pas membre de ce salon"));

        userRoom.setBlocked(true);
        userRoomRepository.save(userRoom);

        return userRoomMapper.toDto(List.of(userRoom));
    }

    public UserRoomDto unBlockUser(Integer userId, Integer roomId) {
        checkAdminAccess(roomId);

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur n'est pas membre de ce salon"));

        userRoom.setBlocked(false);
        userRoomRepository.save(userRoom);

        return userRoomMapper.toDto(List.of(userRoom));
    }

    public void deleteUserFromRoom(Integer userId, Integer roomId) {
        checkAdminAccess(roomId);

        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur n'est pas membre de ce salon"));

        userRoomRepository.delete(userRoom);
    }

    @Transactional(readOnly = true)
    public boolean isUserMemberOfRoom(Integer userId, Integer roomId) {
        return userRoomRepository.existsByUserIdAndRoomId(userId, roomId);
    }

    public List<User> getUsersInRoom(Integer roomId) {
        return userRepository.findAllByRoomId(roomId);
    }
    
    private UserRoom checkAdminAccess(Integer roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer currentUserId = currentUser.getId();
    
        UserRoom currentUserRoom = userRoomRepository.findByUserIdAndRoomId(currentUserId, roomId)
                .orElseThrow(() -> new EntityNotFoundException("Vous n'êtes pas membre de cette salle"));
    
        if (!currentUserRoom.getRoleId().getRole().equalsIgnoreCase("administrator")) {
            throw new AccessDeniedException("Vous n'avez pas les permissions nécessaires.");
        }
    
        return currentUserRoom;
    }
    
}
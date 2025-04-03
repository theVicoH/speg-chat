package com.example.api.services;

import com.example.api.dtos.RoomDto;
import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.exceptions.ApiException;
import com.example.api.mappers.RoomMapper;
import com.example.api.repositories.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Add this import

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final UserRoomService userRoomService;  // Add this dependency

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper, UserRoomService userRoomService) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.userRoomService = userRoomService;  // Initialize it
    }

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public RoomDto getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ApiException("Room not found", HttpStatus.NOT_FOUND));
        return roomMapper.toDto(room);
    }

    @Transactional  // Make this method transactional
    public RoomDto createRoom(RoomDto roomDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User creator = (User) authentication.getPrincipal();
        
        Room room = roomMapper.toEntity(roomDto);
        room.setCreator(creator);
        
        Room savedRoom = roomRepository.save(room);
        
        // Add the creator to the room with administrator role (assuming role ID 1 is administrator)
        // Create a custom method in UserRoomService to add a user with a specific role
        userRoomService.joinRoomWithRole(creator.getId(), savedRoom.getId(), 1); // 1 for administrator role
        
        // Add additional users if specified
        if (roomDto.getUserIds() != null && !roomDto.getUserIds().isEmpty()) {
            for (Integer userId : roomDto.getUserIds()) {
                // Skip if the user ID is the same as the creator (already added)
                if (!userId.equals(creator.getId())) {
                    try {
                        userRoomService.joinRoom(userId, savedRoom.getId());
                    } catch (Exception e) {
                        // Log the error but continue with other users
                        System.err.println("Failed to add user " + userId + " to room: " + e.getMessage());
                    }
                }
            }
        }
        
        return roomMapper.toDto(savedRoom);
    }

    public RoomDto updateRoom(Integer id, RoomDto roomDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ApiException("Room not found", HttpStatus.NOT_FOUND));
        
        roomMapper.updateEntity(room, roomDto);
        Room updatedRoom = roomRepository.save(room);
        return roomMapper.toDto(updatedRoom);
    }

    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new ApiException("Room not found", HttpStatus.NOT_FOUND);
        }
        roomRepository.deleteById(id);
    }

    public void deleteRoom(Integer id, Integer creatorId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ApiException("Room not found", HttpStatus.NOT_FOUND));

        // Vérifier si l'utilisateur est le créateur de la room
        if (!room.getCreator().getId().equals(creatorId)) {
            throw new ApiException("Only the room creator can delete this room", HttpStatus.FORBIDDEN);
        }

        roomRepository.deleteById(id);
    }
}
package com.example.api.services;

import com.example.api.dtos.RoomDto;
import com.example.api.dtos.RoomUpdateDto;
import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.exceptions.ApiException;
import com.example.api.mappers.RoomMapper;
import com.example.api.repositories.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.api.entities.UserRoom;
import com.example.api.repositories.UserRoomRepository;  // Add this import
import org.springframework.transaction.annotation.Transactional; // Add this import

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;  // Add this dependency
    private final RoomMapper roomMapper;
    private final UserRoomService userRoomService;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper, UserRoomService userRoomService , UserRoomRepository userRoomRepository) {
        this.userRoomRepository = userRoomRepository;
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.userRoomService = userRoomService;
    }

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<RoomDto> getRoomsByTypeId(Integer typeId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = (User) authentication.getPrincipal();
    Integer currentUserId = currentUser.getId();

    // Récupérer toutes les salles où l'utilisateur est membre
    List<UserRoom> userRooms = userRoomRepository.findAllByUserId(currentUserId);

    // Filtrer les salles par type
    return userRooms.stream()
            .map(UserRoom::getRoom)
            .filter(room -> room.getType().getId().equals(typeId))  // Filtrer par type
            .map(roomMapper::toDto)
            .collect(Collectors.toList());
    }


    public RoomDto getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ApiException("Room not found", HttpStatus.NOT_FOUND));
        return roomMapper.toDto(room);
    }

    @Transactional
    public RoomDto createRoom(RoomDto roomDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User creator = (User) authentication.getPrincipal();
        
        boolean isPrivateRoom = roomDto.getTypeId() != null && roomDto.getTypeId() == 2;
        
        // For private rooms, ensure there's exactly one other user
        if (isPrivateRoom) {
            if (roomDto.getUserIds() == null || roomDto.getUserIds().isEmpty()) {
                throw new ApiException("Private rooms must have exactly one other user", HttpStatus.BAD_REQUEST);
            }
            
            if (roomDto.getUserIds().size() > 1) {
                throw new ApiException("Private rooms can only have 2 users (you and one other user)", HttpStatus.BAD_REQUEST);
            }
        }
        
        Room room = roomMapper.toEntity(roomDto);
        room.setCreator(creator);
        
        Room savedRoom = roomRepository.save(room);
        
        // Add the creator to the room with administrator role (role ID 1)
        userRoomService.joinRoomWithRole(creator.getId(), savedRoom.getId(), 1);
        
        // Add additional users if specified
        if (roomDto.getUserIds() != null && !roomDto.getUserIds().isEmpty()) {
            // For private rooms, only add the first user from the list
            int maxUsers = isPrivateRoom ? 1 : roomDto.getUserIds().size();
            
            for (int i = 0; i < maxUsers; i++) {
                Integer userId = roomDto.getUserIds().get(i);
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

    public RoomDto updateRoom(Integer id, RoomUpdateDto roomUpdateDto, Integer currentUserId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ApiException("Room not found", HttpStatus.NOT_FOUND));
        
        // Check if the current user is the creator of the room
        if (!room.getCreator().getId().equals(currentUserId)) {
            throw new ApiException("Only the room creator can update this room", HttpStatus.FORBIDDEN);
        }
        
        roomMapper.updateEntityFromUpdateDto(room, roomUpdateDto);
        Room updatedRoom = roomRepository.save(room);
        
        // Add additional users if specified
        if (roomUpdateDto.getUserIds() != null && !roomUpdateDto.getUserIds().isEmpty()) {
            for (Integer userId : roomUpdateDto.getUserIds()) {
                // Skip if the user ID is the same as the creator (already added)
                if (!userId.equals(currentUserId)) {
                    try {
                        // Check if user is already in the room
                        if (!userRoomService.isUserMemberOfRoom(userId, room.getId())) {
                            userRoomService.joinRoom(userId, room.getId());
                        }
                    } catch (Exception e) {
                        // Log the error but continue with other users
                        System.err.println("Failed to add user " + userId + " to room: " + e.getMessage());
                    }
                }
            }
        }
        
        return roomMapper.toDto(updatedRoom);
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
    
    public List<RoomDto> getPublicRooms() {
      // Assuming you have a room type with ID 1 for public rooms
      // You might need to adjust this based on your actual data model
      Integer publicRoomTypeId = 1; // Replace with your actual public room type ID
      return getRoomsByTypeId(publicRoomTypeId);
  }
}
package com.example.api.services;

import com.example.api.dtos.RoomDto;
import com.example.api.entities.Room;
import com.example.api.entities.User;
import com.example.api.entities.RoomType;
import com.example.api.exceptions.ResourceNotFoundException;
import com.example.api.repositories.RoomRepository;
import com.example.api.repositories.UserRepository;
import com.example.api.repositories.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(RoomRepository roomRepository, UserRepository userRepository, RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    /**
     * Crée une nouvelle salle et retourne un DTO.
     */
    @Transactional
    public RoomDto createRoom(RoomDto roomDto) {
        User creator = userRepository.findById(roomDto.getCreator())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + roomDto.getCreator()));

        RoomType roomType = roomTypeRepository.findById(roomDto.getRoomType())
                .orElseThrow(() -> new ResourceNotFoundException("Type de salle non trouvé avec l'ID : " + roomDto.getRoomType()));

        Room room = new Room();
        room.setName(roomDto.getName());
        room.setCreator(creator);
        room.setRoomType(roomType);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());

        Room savedRoom = roomRepository.save(room);
        return convertToDto(savedRoom);
    }


    /**
     * Récupère une salle par ID et retourne un DTO.
     */
    public RoomDto getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID : " + id));
        return convertToDto(room);
    }

    /**
     * Récupère toutes les salles sous forme de liste de DTO.
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms;
    }

    /**
     * Met à jour une salle existante.
     */
    @Transactional
    public RoomDto updateRoom(Integer id, RoomDto roomDto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID : " + id));

        if (roomDto.getName() != null) {
            room.setName(roomDto.getName());
        }

        if (roomDto.getCreator() != null) {
            User creator = userRepository.findById(roomDto.getCreator())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + roomDto.getCreator()));
            room.setCreator(creator);
        }

        if (roomDto.getRoomType() != null) {
            RoomType roomType = roomTypeRepository.findById(roomDto.getRoomType())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de salle non trouvé avec l'ID : " + roomDto.getRoomType()));
            room.setRoomType(roomType);
        }

        room.setUpdatedAt(LocalDateTime.now());
        Room updatedRoom = roomRepository.save(room);

        return convertToDto(updatedRoom);
    }

    /**
     * Supprime une salle par ID.
     */
    @Transactional
    public void deleteRoom(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée avec l'ID : " + id));
        roomRepository.delete(room);
    }

    /**
     * Convertit une entité Room en DTO RoomDto.
     */
    private RoomDto convertToDto(Room room) {
        return new RoomDto(
                room.getId(),
                room.getName(),
                room.getCreator().getId(),
                room.getRoomType().getId(),
                room.getCreatedAt(),
                room.getUpdatedAt(),
                room.getMessages() != null 
                    ? room.getMessages().stream().map(msg -> msg.getId()).collect(Collectors.toList()) 
                    : Collections.emptyList(),
                room.getUsers() != null 
                    ? room.getUsers().stream().map(user -> user.getId()).collect(Collectors.toList()) 
                    : Collections.emptyList()
        );
    }

}

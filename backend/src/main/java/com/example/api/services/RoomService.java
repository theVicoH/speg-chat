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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
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

    public RoomDto createRoom(RoomDto roomDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User creator = (User) authentication.getPrincipal();
        
        Room room = roomMapper.toEntity(roomDto);
        room.setCreator(creator);
        
        Room savedRoom = roomRepository.save(room);
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
}
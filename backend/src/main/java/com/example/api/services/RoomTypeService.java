package com.example.api.services;

import com.example.api.dtos.RoomTypeDto;
import com.example.api.entities.RoomType;
import com.example.api.mappers.RoomTypeMapper;

import com.example.api.repositories.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import com.example.api.exceptions.ResourceNotFoundException;

@Slf4j
@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;

    public RoomTypeService(RoomTypeRepository roomTypeRepository, RoomTypeMapper roomTypeMapper) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomTypeMapper = roomTypeMapper;
    }

    public RoomTypeDto createRoomType(RoomTypeDto roomTypeDto) {
        RoomType roomType = roomTypeMapper.toEntity(roomTypeDto);

        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return roomTypeMapper.toDto(savedRoomType);
    }

    public RoomTypeDto getRoomTypeById(Integer id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de salle non trouvé avec l'ID : " + id));
        return roomTypeMapper.toDto(roomType);
    }

    public List<RoomTypeDto> getAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        return roomTypes.stream()
                .map(roomTypeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public RoomTypeDto updateRoomType(Integer id, RoomTypeDto roomTypeDto) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de salle non trouvé avec l'ID : " + id));

        roomTypeMapper.updateEntity(roomType, roomTypeDto);

        RoomType updatedRoomType = roomTypeRepository.save(roomType);
        return roomTypeMapper.toDto(updatedRoomType);
    }

    public void deleteRoomType(Integer id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Type de salle non trouvé avec l'ID : " + id);
        }
        roomTypeRepository.deleteById(id);
    }

}


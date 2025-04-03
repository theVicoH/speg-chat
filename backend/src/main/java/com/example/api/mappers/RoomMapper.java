package com.example.api.mappers;

import com.example.api.dtos.RoomDto;
import com.example.api.dtos.RoomUpdateDto;
import com.example.api.entities.Room;
import com.example.api.entities.RoomType;
import com.example.api.repositories.RoomTypeRepository;
import com.example.api.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {
    private final RoomTypeRepository roomTypeRepository;

    public RoomMapper(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    public RoomDto toDto(Room room) {
        return new RoomDto()
                .setId(room.getId())
                .setName(room.getName())
                .setTypeId(room.getType().getId())
                .setCreatorId(room.getCreator().getId())
                .setCreatedAt(room.getCreatedAt())
                .setUpdatedAt(room.getUpdatedAt());
    }

    public Room toEntity(RoomDto dto) {
        RoomType type = roomTypeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ApiException("Room type not found", HttpStatus.NOT_FOUND));

        return new Room()
                .setName(dto.getName())
                .setType(type);
    }

    public void updateEntityFromUpdateDto(Room room, RoomUpdateDto dto) {
        // Only update the name, type cannot be changed
        room.setName(dto.getName());
    }
}
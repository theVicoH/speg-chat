package com.example.api.mappers;

import com.example.api.dtos.RoomTypeDto;
import com.example.api.entities.RoomType;
import org.springframework.stereotype.Component;

@Component
public class RoomTypeMapper {

    public RoomTypeDto toDto(RoomType roomType) {
        return new RoomTypeDto()
                .setId(roomType.getId())
                .setType(roomType.getType());
    }

    public RoomType toEntity(RoomTypeDto dto) {
        RoomType roomType = new RoomType();
        roomType.setId(dto.getId());
        roomType.setType(dto.getType());
        return roomType;
    }

    public void updateEntity(RoomType roomType, RoomTypeDto dto) {
        roomType.setType(dto.getType());
    }
}
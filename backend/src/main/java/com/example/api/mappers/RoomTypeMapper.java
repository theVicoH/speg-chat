package com.example.api.mappers;

import com.example.api.dtos.RoomTypeDto;
import com.example.api.entities.RoomType;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
public class RoomTypeMapper {

    public RoomTypeDto toDto(RoomType roomType) {
        return new RoomTypeDto()
                .setId(roomType.getId())
                .setType(roomType.getType());
    }

    public RoomType toEntity(RoomTypeDto dto) {
        return new RoomType()
                .setId(dto.getId())
                .setType(dto.getType());
    }

    public void updateEntity(RoomType roomType, RoomTypeDto dto) {
        roomType.setType(dto.getType());
    }
}
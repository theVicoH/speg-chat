package com.example.api.mappers;

import com.example.api.dtos.UserRoomDto;
import com.example.api.entities.UserRoom;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserRoomMapper {

    public UserRoomDto toDto(List<UserRoom> userRooms) {
        if (userRooms.isEmpty()) {
            throw new IllegalArgumentException("Aucun utilisateur trouvé pour cette salle");
        }

        String roomName = userRooms.get(0).getRoom().getName();

        List<UserRoomDto.UserInfo> users = userRooms.stream()
                .map(ur -> new UserRoomDto.UserInfo()
                        .setUsername(ur.getUser().getUsername())
                        .setRole(ur.getRoleId().getRole()))
                .collect(Collectors.toList());

        return new UserRoomDto()
                .setRoomName(roomName)
                .setUsers(users);
    }
}

package com.example.api.mappers;

import com.example.api.dtos.UserDto;
import com.example.api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto()
                .setId(user.getId())
                .setUsername(user.getUsername());
    }

    public User toEntity(UserDto dto) {
        return new User()
                .setUsername(dto.getUsername())
                .setPassword(dto.getPassword());
    }
}
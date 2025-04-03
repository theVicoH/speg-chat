package com.example.api.controllers;

import com.example.api.dtos.UserDto;
import com.example.api.entities.User;
import com.example.api.mappers.UserMapper;
import com.example.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userMapper.toDto(currentUser));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.allUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserDto userDto
    ) {
        User user = userMapper.toEntity(userDto);
        user.setId(id);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Add this new endpoint
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@Valid @RequestBody UserDto userDto) {
        // Get the current authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Integer userId = currentUser.getId();
        
        // Use the existing update logic with the current user's ID
        User user = userMapper.toEntity(userDto);
        user.setId(userId);
        User updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }
}
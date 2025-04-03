package com.example.api.controllers;

import com.example.api.dtos.RoomDto;
import com.example.api.entities.User;
import com.example.api.services.RoomService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<RoomDto>> getRoomsByTypeId(@PathVariable Integer typeId) {
        return ResponseEntity.ok(roomService.getRoomsByTypeId(typeId));
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(
            @Valid @RequestBody RoomDto roomDto
    ) {
        return ResponseEntity.ok(roomService.createRoom(roomDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable Integer id,
            @Valid @RequestBody RoomDto roomDto
    ) {
        return ResponseEntity.ok(roomService.updateRoom(id, roomDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();
        roomService.deleteRoom(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}



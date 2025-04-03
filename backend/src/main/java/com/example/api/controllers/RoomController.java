package com.example.api.controllers;

import com.example.api.dtos.JoinRoomDto;
import com.example.api.dtos.RoomDto;
import com.example.api.entities.User;
import com.example.api.entities.UserRoom;
import com.example.api.services.RoomService;
import com.example.api.services.UserRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final UserRoomService userRoomService;

    public RoomController(RoomService roomService, UserRoomService userRoomService) {
        this.roomService = roomService;
        this.userRoomService = userRoomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
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

    @Operation(summary = "Rejoindre un salon", description = "Permet à l'utilisateur authentifié de rejoindre un salon existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "L'utilisateur a rejoint le salon avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide ou l'utilisateur est déjà membre du salon"),
        @ApiResponse(responseCode = "404", description = "Salon non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping("/{roomId}/join")
    public ResponseEntity<UserRoom> joinRoom(
            @PathVariable Integer roomId,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        Integer userId = currentUser.getId();
        
        log.info("Demande de l'utilisateur {} pour rejoindre le salon {}", userId, roomId);
        
        UserRoom userRoom = userRoomService.joinRoom(userId, roomId);
        
        return new ResponseEntity<>(userRoom, HttpStatus.OK);
    }
}



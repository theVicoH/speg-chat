package com.example.api.controllers;

import com.example.api.dtos.JoinRoomDto;
import com.example.api.entities.User;
import com.example.api.entities.UserRoom;
import com.example.api.dtos.UserRoomDto;
import com.example.api.services.UserRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user-rooms")
@RequiredArgsConstructor
@Slf4j
public class UserRoomController {
    
    private final UserRoomService userRoomService;
    
    @Operation(summary = "Rejoindre un salon", description = "Permet à l'utilisateur authentifié de rejoindre un salon existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "L'utilisateur a rejoint le salon avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide ou l'utilisateur est déjà membre du salon"),
        @ApiResponse(responseCode = "404", description = "Salon non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping("/join")
    public ResponseEntity<UserRoom> joinRoom(
            @Valid @RequestBody JoinRoomDto joinRoomDto,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        Integer userId = currentUser.getId();
        Integer roomId = joinRoomDto.getRoomId();
        
        log.info("Demande de l'utilisateur {} pour rejoindre le salon {}", userId, roomId);
        
        UserRoom userRoom = userRoomService.joinRoom(userId, roomId);
        
        return new ResponseEntity<>(userRoom, HttpStatus.OK);
    }
    
    @Operation(summary = "Quitter un salon", description = "Permet à l'utilisateur authentifié de quitter un salon")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "L'utilisateur a quitté le salon avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide ou l'utilisateur n'est pas membre du salon"),
        @ApiResponse(responseCode = "404", description = "Salon non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/leave")
    public ResponseEntity<Void> leaveRoom(
            @Valid @RequestBody JoinRoomDto leaveRoomDto,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        Integer userId = currentUser.getId();
        Integer roomId = leaveRoomDto.getRoomId();
        
        log.info("Demande de l'utilisateur {} pour quitter le salon {}", userId, roomId);
        
        userRoomService.leaveRoom(userId, roomId);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<UserRoomDto> getUserRoom(@PathVariable Integer roomId) {
        UserRoomDto userRoom = userRoomService.getUserRoomByRoom(roomId);
        return ResponseEntity.ok(userRoom);
    }
}
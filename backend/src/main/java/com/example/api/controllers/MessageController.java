package com.example.api.controllers;

import com.example.api.dtos.MessageDto;
import com.example.api.dtos.MessageRequest;
import com.example.api.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDto>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Integer id) { // Changé de Long à Integer
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MessageDto>> getMessagesByRoomId(@PathVariable Integer roomId) { // Changé de Long à Integer
        return ResponseEntity.ok(messageService.getMessagesByRoomId(roomId));
    }

    @PostMapping
    public ResponseEntity<MessageDto> createMessage(@Valid @RequestBody MessageRequest messageRequest) {
        return ResponseEntity.ok(messageService.createMessage(messageRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable Integer id, // Changé de Long à Integer
            @Valid @RequestBody MessageRequest messageRequest) {
        return ResponseEntity.ok(messageService.updateMessage(id, messageRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) { // Changé de Long à Integer
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
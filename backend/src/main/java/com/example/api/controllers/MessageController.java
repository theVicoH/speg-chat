package com.example.api.controllers;

import com.example.api.dtos.MessageDto;
import com.example.api.dtos.MessageRequest;
import com.example.api.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Integer id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MessageDto>> getMessagesByRoomId(@PathVariable Integer roomId) {
        List<MessageDto> messages = messageService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<MessageDto> createMessage(@Valid @RequestBody MessageRequest messageRequest) {
        return ResponseEntity.ok(messageService.createMessage(messageRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable Integer id,
            @Valid @RequestBody MessageRequest messageRequest) {
        return ResponseEntity.ok(messageService.updateMessage(id, messageRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
package com.example.api.controllers;

import com.example.api.dtos.MessageDto;
import com.example.api.dtos.MessageRequest;
import com.example.api.services.MessageService;
import com.example.api.services.JwtService;
import com.example.api.repositories.UserRepository;
import com.example.api.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public MessageDto sendMessage(
            @DestinationVariable Integer roomId,
            @Payload MessageRequest messageRequest,
            @Header("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtService.extractUsername(jwt);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        
        try {
            messageRequest.setRoomId(roomId);
            return messageService.createMessage(messageRequest);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
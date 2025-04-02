package com.example.api.services;

import com.example.api.dtos.LoginUserDto;
import com.example.api.dtos.RegisterUserDto;
import com.example.api.entities.User;
import com.example.api.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        // Check if username already exists
        if (userRepository.findByUsername(input.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        
        User user = new User()
                .setUsername(input.getUsername())
                .setPassword(passwordEncoder.encode(input.getPassword()));
    
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // This is a fallback in case of race conditions
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }
}
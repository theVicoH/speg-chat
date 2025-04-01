package com.example.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Tentative de connexion à la base de données...");
            String dbVersion = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            
            log.info("Connexion à la base de données réussie. Version: {}", dbVersion);
            response.put("status", "connected");
            response.put("dbVersion", dbVersion);
            response.put("message", "La connexion à la base de données est établie");
        } catch (Exception e) {
            log.error("Échec de la connexion à la base de données: {}", e.getMessage(), e);
            response.put("status", "disconnected");
            response.put("error", e.getMessage());
            response.put("message", "Impossible de se connecter à la base de données");
        }
        
        return ResponseEntity.ok(response);
    }
}
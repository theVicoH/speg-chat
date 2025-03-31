package com.example.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Hello", description = "API de démonstration pour tester le fonctionnement de base")
public class HelloController {

    @Operation(summary = "Dire bonjour", description = "Endpoint simple qui renvoie un message de salutation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message de salutation récupéré avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

}

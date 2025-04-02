package com.example.api.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // Utilisez le bon nom de colonne
    @Column(name = "message", nullable = false)
    private String content;
    
    @ManyToOne(fetch = FetchType.EAGER)  // Changez LAZY en EAGER
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)  // Changez LAZY en EAGER
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Méthode utilitaire pour obtenir l'ID de la room
    public Integer getRoomId() {
        return room != null ? room.getId() : null;
    }
    
    // Ajouter des méthodes pour gérer les timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
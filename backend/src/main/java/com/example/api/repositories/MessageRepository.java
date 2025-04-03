package com.example.api.repositories;

import com.example.api.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    // Utiliser la syntaxe correcte pour accéder à l'ID via la relation
    List<Message> findByRoom_Id(Integer roomId);
}
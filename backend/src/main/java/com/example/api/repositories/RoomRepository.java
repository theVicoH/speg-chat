package com.example.api.repositories;

import com.example.api.entities.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface RoomRepository extends CrudRepository<Room, Integer> {
    
    // Trouver une salle par son ID
    Optional<Room> findById(Integer id);

    // Vérifier si une salle existe par son ID
    boolean existsById(Integer id);

    // Supprimer une salle par son ID
    void deleteById(Integer id);

    List<Room> findAll();

    // Enregistrer ou mettre à jour une salle (déjà géré par CrudRepository)
    Room save(Room room);
}

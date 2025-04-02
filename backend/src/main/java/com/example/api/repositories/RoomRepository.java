package com.example.api.repositories;

import com.example.api.entities.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface RoomRepository extends CrudRepository<Room, Integer> {
    
    Optional<Room> findById(Integer id);

    boolean existsById(Integer id);

    void deleteById(Integer id);

    List<Room> findAll();

    Room save(Room room);
}

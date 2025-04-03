package com.example.api.repositories;

import com.example.api.entities.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, Integer> {
    List<Room> findAll();
}

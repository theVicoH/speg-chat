package com.example.api.repositories;

import com.example.api.entities.Room;
import com.example.api.entities.RoomType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, Integer> {
    List<Room> findAll();
    List<Room> findByType(RoomType type);
    List<Room> findByTypeId(Integer typeId);
}

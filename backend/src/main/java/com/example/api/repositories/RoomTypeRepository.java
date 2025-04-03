package com.example.api.repositories;

import com.example.api.entities.RoomType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RoomTypeRepository extends CrudRepository<RoomType, Integer> {
    
    Optional<RoomType> findById(Integer id);

    boolean existsById(Integer id);

    void deleteById(Integer id);

    @Override
    List<RoomType> findAll();
}

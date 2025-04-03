package com.example.api.repositories;

import com.example.api.entities.UserRoom;
import com.example.api.entities.User;
import com.example.api.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

import javax.management.relation.Role;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    boolean existsByUserIdAndRoomId(Integer userId, Integer roomId);
    Optional<UserRoom> findByUserIdAndRoomId(Integer userId, Integer roomId);
    long countByRoomId(Integer roomId);
    Optional<UserRoom> findByRoomId(Integer roomId);
    List<UserRoom> findAllByRoomId(Integer roomId);
    Optional<UserRoom> findByUserIdAndRoomIdAndRoleId(Integer userId, Integer roomId, Role roleId);
}
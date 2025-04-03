package com.example.api.controllers;

import com.example.api.dtos.RoomTypeDto;
import com.example.api.services.RoomTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @PostMapping
    public ResponseEntity<RoomTypeDto> createRoomType(@RequestBody RoomTypeDto roomTypeDto) {
        RoomTypeDto createdRoomType = roomTypeService.createRoomType(roomTypeDto);
        return new ResponseEntity<>(createdRoomType, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeDto> getRoomTypeById(@PathVariable Integer id) {
        RoomTypeDto roomType = roomTypeService.getRoomTypeById(id);
        return new ResponseEntity<>(roomType, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RoomTypeDto>> getAllRoomTypes() {
        List<RoomTypeDto> roomTypes = roomTypeService.getAllRoomTypes();
        return new ResponseEntity<>(roomTypes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeDto> updateRoomType(@PathVariable Integer id, @RequestBody RoomTypeDto roomTypeDto) {
        RoomTypeDto updatedRoomType = roomTypeService.updateRoomType(id, roomTypeDto);
        return new ResponseEntity<>(updatedRoomType, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Integer id) {
        roomTypeService.deleteRoomType(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}




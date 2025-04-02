package com.example.api.dtos;

public class RoomTypeDto {
    private Long id;
    private String type;

    // Constructeurs
    public RoomTypeDto() {}

    public RoomTypeDto(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
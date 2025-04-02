package com.example.api.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegisterUserDto {
    private String username;
    private String password;
}
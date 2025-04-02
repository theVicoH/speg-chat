package com.example.api.dtos;

public class RegisterUserDto {
    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public RegisterUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public RegisterUserDto setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String toString() {
        return "RegisterUserDto{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
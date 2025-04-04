package com.example.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserBlockedException extends RuntimeException {
    public UserBlockedException(String message) {
        super(message);
    }
}


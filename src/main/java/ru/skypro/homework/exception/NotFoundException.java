package ru.skypro.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super();
    }

    public NotFoundException() {
    }
}

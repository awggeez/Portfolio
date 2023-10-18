package com.kpo.restaurant_project.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class InvalidAuthDataException extends Exception {
    HttpStatus status;
    String message;
}

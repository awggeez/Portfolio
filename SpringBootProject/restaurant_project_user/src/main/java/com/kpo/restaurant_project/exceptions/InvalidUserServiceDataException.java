package com.kpo.restaurant_project.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class InvalidUserServiceDataException extends Exception {
    private HttpStatus status;
    private String message;
}
